package com.coloplast.olympus.bluetooth

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.model.*
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
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
  private val deviceConnection: DeviceConnection
) : ServiceViewModel<BluetoothServiceModel, BluetoothServiceResult<BluetoothServiceModel>>() {

  override fun bind(serviceResult: BluetoothServiceResult<BluetoothServiceModel>): Disposable {
    val compositeDisposable = CompositeDisposable()

    val retryObservable = ServiceDispatcher.serviceEventObservable()
      .filter { it is ServiceEvent.RetryConnection }
      .map { true }

    val databaseTransmittersObservable = transmitterComponent.getTransmitters()
      .subscribeOn(Schedulers.io())
      .doOnNext { Timber.d("databaseTransmittersObservable - change happened") }

    val immediateConnectionObservable = Observable.merge(
      Observable.interval(1, 30, TimeUnit.SECONDS).doOnNext { Timber.d("Hitting interval $it") }.map { true },
      databaseTransmittersObservable.map { true },
      retryObservable
    )
      .doOnNext { Timber.d("Fetch transmitters") }
      .flatMap {
        transmitterComponent.getTransmittersAsSingle()
          .toObservable()
      }
      .filter { it.isNotEmpty() }
      .flatMapIterable { it }
      .map { Pair(it, bluetoothManager.bleConnectionStateObservable(it.id).blockingFirst()) }
      .doOnNext { Timber.d("[${it.first.name}] - [${it.first.id}] - is ${it.second}") }
      .filter { it.second == BleConnectionState.DISCONNECTED }
      .map { it.first }
      .zipWith(Observable.interval(5, TimeUnit.SECONDS),
        { transmitter, _ -> transmitter })
      .map { it.id }
      .publish()
      .refCount()

    val disconnectTransmitterObservable = ServiceDispatcher.serviceEventObservable()
      .filter { it is ServiceEvent.DisconnectTransmitter }
      .map { it as ServiceEvent.DisconnectTransmitter }
      .delay(5, TimeUnit.SECONDS)

    val connectionObservable = immediateConnectionObservable
      .filter { bluetoothManager.bleConnectionStateObservable(it).blockingFirst() == BleConnectionState.DISCONNECTED }
      .flatMap { connectObservable(it) }

    compositeDisposable.add(connectionObservable.bind(serviceResult.onResult()))
    compositeDisposable.add(disconnectTransmitterObservable.subscribe())

    return compositeDisposable
  }

  private fun connectObservable(macAddress: String) : Observable<BluetoothServiceModel> {
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
      .doOnError {
        ServiceDispatcher.dispatchEvent(ServiceEvent.DisconnectTransmitter(macAddress))
      }
      .onErrorReturn {
        // return a disconnect to the service
        BluetoothServiceModel.Disconnected(macAddress)
      }
  }
}