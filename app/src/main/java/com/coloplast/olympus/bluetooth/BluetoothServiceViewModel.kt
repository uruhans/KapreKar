package com.coloplast.olympus.bluetooth

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.model.*
import com.coloplast.olympus.businesslayer.interfaces.AppStateComponent
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.ui.feature.home.models.StateTransformers
import com.coloplast.olympus.ui.feature.statemachine.OverlayStateMachineImpl
import com.coloplast.olympus.ui.feature.statemachine.UiStateMachineImpl
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import com.coloplast.olympus.utils.DeviceUtils
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class BluetoothServiceViewModel(
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent,
  private val deviceConnection: DeviceConnection,
  private val appStateComponent: AppStateComponent
) : ServiceViewModel<BluetoothServiceModel, BluetoothServiceResult<BluetoothServiceModel>>() {

  override fun bind(serviceResult: BluetoothServiceResult<BluetoothServiceModel>): Disposable {
    val compositeDisposable = CompositeDisposable()

    // this observable pings us when we want to retry a connection attempt
    // and is usually started by the user elsewhere in the app
    val retryObservable = ServiceDispatcher.serviceEventObservable()
      .filter { it is ServiceEvent.RetryConnection }
      .map { true }

    // this observable notifies us of changes occurring in the local database
    // meaning, it pings us when transmitters are added and removed from database
    // it is a *hot* observable, and will continue to notify us always
    val databaseTransmittersObservable = transmitterComponent.getTransmitters()
      .subscribeOn(Schedulers.io())
      // however, we dont really care about the result, we just want the ping
      .map { true }

    // this is a continuous ping that notifies us every 30 seconds
    val continuousPingObservable = Observable.interval(30, TimeUnit.SECONDS)
      .map { true }

    // we merge these these pings, that will notify us that we want to try
    // to connect to our transmitters
    val immediateConnectionObservable = Observable.merge(
      continuousPingObservable,
      databaseTransmittersObservable,
      retryObservable
    )
      .flatMap {
        // here we get all transmitters stored locally in the database
        // we are doing it as a single because we dont want it to notify us when database changes occur
        transmitterComponent.getTransmittersAsSingle()
          .subscribeOn(Schedulers.io())
          .toObservable()
      }
      // only begin connection flow if we actually have some transmitters to connect to
      .filter { it.isNotEmpty() }
      // split transmitters into single transmitters (if we have more than one)
      .flatMapIterable { it }
      // get the current connection state of the transmitter immediately
      .map { TxModel(it, bluetoothManager.bleConnectionStateObservable(it.id).blockingFirst()) }
      // write it out to logcat
      .doOnNext { Timber.d("[${it.transmitter.name}] - [${it.transmitter.id}] - is ${it.connectionState}") }
      // only continue if the current transmitter is disconnected
      .filter { it.connectionState == BleConnectionState.DISCONNECTED }
      // then map the txModel => transmitter
      .map { it.transmitter }
      // then map the transmitter => macAddress
      .map { it.id }
      .publish()
      .refCount()

    val disconnectTransmitterObservable = ServiceDispatcher.serviceEventObservable()
      .filter { it is ServiceEvent.DisconnectTransmitter }
      .map { it as ServiceEvent.DisconnectTransmitter }
      .delay(5, TimeUnit.SECONDS)

    // the observable that handles the connection
    val connectionObservable = immediateConnectionObservable
      // just as a precaution, we check the connection state for the transmitter again
      // must be disconnected
      .filter { bluetoothManager.bleConnectionStateObservable(it).blockingFirst() == BleConnectionState.DISCONNECTED }
      // then we do the actual connection
      .flatMap { connectObservable(it) }

    compositeDisposable.add(connectionObservable.bind(serviceResult.onResult()))
    compositeDisposable.add(disconnectTransmitterObservable.subscribe())

    return compositeDisposable
  }

  private fun connectObservable(macAddress: String): Observable<BluetoothServiceModel> {
    val stateMachine = UiStateMachineImpl()
    val overlayStateMachine = OverlayStateMachineImpl()

    return deviceConnection.subscribeForNotifications(macAddress, DeviceUtils.versionName())
      .map { deviceResult ->
        when (deviceResult) {
          is DeviceResult.DeviceUpdateResult -> {
            val stateModel = StateTransformers.bleToUiState(deviceResult)
            val newUiState = stateMachine.invoke(stateModel.state)
            val overlayStateModel = StateTransformers.bleToOverlayState(deviceResult)
            val newOverlayState = overlayStateMachine.invoke(overlayStateModel.overlayState)

            BluetoothServiceModel.DeviceUpdate(StateChange(newUiState, newOverlayState, deviceResult.bleDevice))
          }
          is DeviceResult.RawDataPackageResult -> {
            BluetoothServiceModel.RawDataPackageUpdate(deviceResult.macAddress, deviceResult.rawDataPackage)
          }
          is DeviceResult.Error -> {
            // return a disconnect to the service
            BluetoothServiceModel.Disconnected(macAddress)
          }
          is DeviceResult.FirmwareAvailable -> {
            BluetoothServiceModel.FirmwareUpdateAvailable(StateChange(UiState.FIRMWARE_AVAILABLE, bleDevice = deviceResult.bleDevice))
          }
          is DeviceResult.AppUpdateAvailable -> {
            BluetoothServiceModel.AppUpdateAvailable(StateChange(UiState.APP_UPDATE_AVAILABLE, bleDevice = deviceResult.bleDevice))
          }
        }
      }
      .doOnNext {
        val uiState = when(it) {
          is BluetoothServiceModel.Disconnected -> {
            UiState.DISCONNECTED
          }
          is BluetoothServiceModel.DeviceUpdate -> {
            it.statusChange.state
          }
          else -> null
        }

        uiState?.let { state ->
          appStateComponent.insertAppState(state.toLetter(), AppSettings.appInBackground)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Timber.e(throwable) }
            .blockingGet()
        }
      }
      .doOnError {
        ServiceDispatcher.dispatchEvent(ServiceEvent.DisconnectTransmitter(macAddress))
      }
      .onErrorReturn {
        // return a disconnect to the service
        BluetoothServiceModel.Disconnected(macAddress)
      }
  }

}