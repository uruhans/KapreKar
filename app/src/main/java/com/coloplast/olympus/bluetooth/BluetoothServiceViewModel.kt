package com.coloplast.olympus.bluetooth

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.model.*
import com.coloplast.olympus.businesslayer.interfaces.AppStateComponent
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
import java.util.concurrent.atomic.AtomicBoolean

class BluetoothServiceViewModel(
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent,
  private val deviceConnection: DeviceConnection,
  private val appStateComponent: AppStateComponent
) : ServiceViewModel<BluetoothServiceModel, BluetoothServiceResult<BluetoothServiceModel>>() {

  private val reversed = AtomicBoolean(false)
  private val shouldReconnect = AtomicBoolean(true)

  override fun bind(serviceResult: BluetoothServiceResult<BluetoothServiceModel>): Disposable {
    val compositeDisposable = CompositeDisposable()

    val bluetoothStateObservable = bluetoothManager.bluetoothStateObservable()
      .filter { it == BluetoothState.BLUETOOTH_NOT_ENABLED }
      .doOnNext { insertAppStateInDb(UiState.BLUETOOTH_NOT_ENABLED.toLetter()) }

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

    // this is a continuous ping that notifies us every 12 seconds
    val continuousPingObservable = Observable.interval(12, TimeUnit.SECONDS)
      .map { true }

    // we merge these these pings, that will notify us that we want to try
    // to connect to our transmitters
    val immediateConnectionObservable = Observable.merge(
      continuousPingObservable,
      databaseTransmittersObservable,
      retryObservable
    )
      .filter { bluetoothManager.bluetoothStateObservable().blockingFirst() == BluetoothState.READY}
      // only attempt reconnect if we want to
      .filter { shouldReconnect.get() }
      .flatMap {
        // here we get all transmitters stored locally in the database
        // we are doing it as a single because we dont want it to notify us when database changes occur
        transmitterComponent.getTransmittersAsSingle()
          .map { if (reversed.get()) it.reversed() else it }
          .subscribeOn(Schedulers.io())
          .toObservable()
          .doOnNext { reversed.set(!reversed.get()) }
      }
      // only begin connection flow if we actually have some transmitters to connect to
      .filter { it.isNotEmpty() }
      // split transmitters into single transmitters (if we have more than one)
      .flatMapIterable { it }
      .throttleLatest(5, TimeUnit.SECONDS)
      // get the current connection state of the transmitter immediately
      .map { TxModel(it, bluetoothManager.bleConnectionStateObservable(it.id).blockingFirst()) }
      //.doOnNext { Timber.d("Device [${it.transmitter.name}] is [${it.connectionState}]") }
      // only continue if the current transmitter is disconnected
      .filter { it.connectionState == BleConnectionState.DISCONNECTED }
      //.doOnNext { Timber.d("Scanning for [${it.transmitter.name}] [${it.transmitter.id}]") }
      .flatMap { txModel ->
        bluetoothManager.scanSpecificTransmitter(txModel.transmitter.id)
          .takeUntil(Observable.timer(3, TimeUnit.SECONDS))
          .takeUntil { it == txModel.transmitter.id }
          .take(1)
          .map { true }
          .onErrorReturn { false }
          .filter { it }
          .map { txModel }
      }
      .delay(1, TimeUnit.SECONDS)
      .doOnNext { Timber.wtf("Trying to connect to [${it.transmitter.name}] with macAddress: [${it.transmitter.id}]") }
      // then map the txModel => transmitter
      .map { it.transmitter }
      // then map the transmitter => macAddress
      .map { it.id }
//      .publish()
//      .refCount()

    /**
     * There is a case where the reconnection attempts are preventing users from adding a 2nd transmitter.
     *
     * This occurs when the 1st transmitter is disconnected, and we try to reconnect every 6 seconds, so
     * this observable was created in an attempt to stop reconnection when the user wants to add a new transmitter
     */
    val reconnectionAttemptsObservable = ServiceDispatcher.serviceEventObservable()
      .ofType(ServiceEvent.ReconnectionAttempt::class.java)
      .doOnNext { Timber.d("reconnectionAttemptsObservable was hit shouldReconnect=[${it.shouldReconnect}]") }
      .doOnNext { shouldReconnect.set(it.shouldReconnect) }

    val disconnectTransmitterObservable = ServiceDispatcher.serviceEventObservable()
      .filter { it is ServiceEvent.DisconnectTransmitter }
      .map { it as ServiceEvent.DisconnectTransmitter }
      //.delay(2000, TimeUnit.MILLISECONDS)

    // the observable that handles the connection
    val connectionObservable = immediateConnectionObservable
      // just as a precaution, we check the connection state for the transmitter again
      // must be disconnected
      .filter { bluetoothManager.bleConnectionStateObservable(it).blockingFirst() == BleConnectionState.DISCONNECTED }
      // then we do the actual connection
      .flatMap { macAddress ->
        connectObservable(macAddress)
        .takeUntil(
          disconnectTransmitterObservable
            .filter { it.macAddress == macAddress }
        )
        // added to be completely sure that we are terminating the connection
        .onTerminateDetach()
        // log disconnected state
        .doOnNext {
          if (it is BluetoothServiceModel.Disconnected) {
            insertAppStateInDb(UiState.DISCONNECTED.toLetter())
          }
        }
      }

    compositeDisposable.add(bluetoothStateObservable.subscribe())
    compositeDisposable.add(connectionObservable.bind(serviceResult.onResult()))
    compositeDisposable.add(reconnectionAttemptsObservable.subscribe())

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

            val stateChange = StateChange(newUiState, newOverlayState, deviceResult.bleDevice)

            /**
             * The reason for doing this is to try to mitigate multiple callbacks
             * that will seem like a "real" statusVector, but in fact is only a battery update (containing the old statusVector)
             * which could go and mess things up for the APS system (but we still want to send the battery updates to the screens)
             */
            if(deviceResult.bleDevice.peripheral?.isBatteryUpdate == true) {
              BluetoothServiceModel.BatteryUpdate(stateChange)
            }
            else {
              BluetoothServiceModel.DeviceUpdate(stateChange)
            }
          }
          is DeviceResult.CompromisedPackageResult -> {
            BluetoothServiceModel.CompromisedPackageUpdate(deviceResult.macAddress, deviceResult.compromisedPackage)
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
        val stateLetter = when(it) {
          is BluetoothServiceModel.DeviceUpdate -> {
            it.statusChange.state.toLetter()
          }
          is BluetoothServiceModel.BatteryUpdate -> {
            it.statusChange.overlayState.toLetter()
          }
          else -> null
        }

        stateLetter?.let { letter ->
          insertAppStateInDb(letter)
        }
      }
      .onErrorReturn {
        // return a disconnect to the service
        BluetoothServiceModel.Disconnected(macAddress)
      }
  }

  private fun insertAppStateInDb(stateLetter: String): Throwable? {
    return appStateComponent.insertAppState(stateLetter, logFromService = true)
      .subscribeOn(Schedulers.io())
        //LifecycleObserver has a 700 ms delay
      .delay(800, TimeUnit.MILLISECONDS)
      .doOnError { throwable -> Timber.e(throwable) }
      .blockingGet()
  }


}