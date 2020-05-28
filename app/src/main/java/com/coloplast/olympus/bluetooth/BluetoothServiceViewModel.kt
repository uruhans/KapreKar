package com.coloplast.olympus.bluetooth

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.model.*
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.home.models.StateTransformers
import com.coloplast.olympus.ui.feature.statemachine.OverlayStateMachineImpl
import com.coloplast.olympus.ui.feature.statemachine.UiStateMachineImpl
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class BluetoothServiceViewModel(
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent,
  private val deviceConnection: DeviceConnection,
  private val transmitterManager: TransmitterManager
) : ServiceViewModel<BluetoothServiceModel, BluetoothServiceResult<BluetoothServiceModel>>() {

  private val connectedTransmittersRelay = PublishRelay.create<List<String>>()

  // the repeatIntervalObservable to determine when to repeat something
  private fun repeatIntervalObservable(seconds: Long) = Observable.interval(seconds, TimeUnit.SECONDS)

  override fun bind(serviceResult: BluetoothServiceResult<BluetoothServiceModel>): Disposable {
    val compositeDisposable = CompositeDisposable()

    val disposables = mutableMapOf<String, Disposable>()

    val connectedBleDevicesList = mutableListOf<String>()

    val connectedBleDevicesObservable = connectedTransmittersRelay
      .publish()
      .refCount()
      .startWith(connectedBleDevicesList)

    val currentBluetoothStateObservable = bluetoothManager
      .bluetoothStateObservable()
      .skip(1)

    val bluetoothStateReadyObservable = currentBluetoothStateObservable
      .filter { it == BluetoothState.READY }

    val databaseTransmittersObservable = transmitterComponent.getTransmitters()
      .subscribeOn(Schedulers.io())
      .doOnNext { Timber.d("databaseTransmitters hit $it") }

    val dbTransmittersObservable = databaseTransmittersObservable
      .map {
        if (it is DataResult.Success<*>) {
          it.data as List<Transmitter>
        }
        else {
          emptyList()
        }
      }

    val allTransmittersConnectedObservable = dbTransmittersObservable
      .flatMap { dbTransmitters ->
        connectedBleDevicesObservable
          .map { it.size == dbTransmitters.size }
      }

    // this observable handles scanning for beacons
    // first the bluetooth state must obviously be ready
    val bleBeaconScanningObservable = bluetoothStateReadyObservable
      .flatMap {
        // then we check if we are already connected to all
        allTransmittersConnectedObservable
          // we dont want any more results from this guy unless the bluetoothstateReadyObservable hits *Ready* state again
          .take(1)
          // if we are not connected to all, then continue
          .filter { allConnected -> !allConnected }
          .flatMap {
            // scan for beacons
            bluetoothManager.scanBeacons()
              .doOnNext { Timber.d("BEACON - beacon found $it") }
              // stop scanning when
              // 1. all transmitters are connected
              // 2. bluetooth is *not* ready anymore
              .takeUntil(
                Observable.merge(
                  // all transmitters are connected
                  allTransmittersConnectedObservable.doOnNext { Timber.d("BEACON allConnected $it") }.filter { allConnected -> allConnected},
                  // bluetooth state no longer ready
                  currentBluetoothStateObservable.doOnNext { Timber.d("BEACON currentBluetoothState $it") }.filter { it != BluetoothState.READY }
                ).doOnNext { Timber.d("BEACON Stopped Scanning") })
              // only return distinct beacons so we prevent duplicates getting through all the time
              .distinctUntilChanged()
              .doOnNext { Timber.d("BEACON Beacon found: $it") }
              .flatMap { beaconAddress ->
                // now we need to figure out if the found beacon is one of "ours" - meaning, have we added it manually before?
                dbTransmittersObservable
                  // so our database transmitter *must* contain the found beacon
                  .filter { dbTransmitters -> dbTransmitters.any { it.macAddress == beaconAddress } }
                  .doOnNext { Timber.d("BEACON Beacon is one of ours - lets try to connect") }
                  // pass through the address in order to start connecting
                  .map { beaconAddress }
              }
          }
      }

    // get all database transmitters
    val immediateConnectionObservable = dbTransmittersObservable
      // convert the list of transmitters into single items to be emitted
      .flatMapIterable { it }
      .flatMap { dbTransmitter ->
        connectedBleDevicesObservable
          // make sure we are not already connected to it
          .filter { connectedDevices -> connectedDevices.none { it == dbTransmitter.macAddress } }
          .map { dbTransmitter.macAddress }
      }

    // listen to service events for any retrys
    val retryConnectionObservable = ServiceDispatcher.serviceEventObservable()
      .filter { it == ServiceEvent.RETRY_CONNECTION }
      .switchMap { bleBeaconScanningObservable }

    val beaconScanningWithRetryObservable = Observable.merge(retryConnectionObservable, bleBeaconScanningObservable)

    // merging beacon scannings and database transmitters in order to
    // provide a list of transmitters to connect to
    val devicesToConnectToObservable = Observable.merge(
      beaconScanningWithRetryObservable,
      immediateConnectionObservable
    )
      .filter { connectedBleDevicesList.none { address -> address == it } }
      // add delay to each onNext to allow for device to be connected to not overload the BLE stack
      .zipWith(repeatIntervalObservable(5),
        BiFunction { macAddress: String, _: Long -> macAddress })

    val connectTransmitterObservable = devicesToConnectToObservable
      .doOnNext { connectedBleDevicesList.add(it) }
      .flatMap { macAddress ->

        bluetoothManager.bleConnectionStateObservable(macAddress)
          .doOnNext {
            if (it == BleConnectionState.CONNECTED) {
              if (connectedBleDevicesList.none { address -> address == macAddress }) {
                connectedTransmittersRelay.accept(connectedBleDevicesList)
              }
            } else if (it == BleConnectionState.DISCONNECTED) {
              connectedTransmittersRelay.accept(connectedBleDevicesList)
            }
          }
          .filter { it == BleConnectionState.DISCONNECTED }
          .take(1)
          .doOnNext { Timber.d("trying to connect to $macAddress") }
          .flatMap {
            connectObservable(macAddress)
              .doOnNext {
                if(it is BluetoothServiceModel.Disconnected) {
                  connectedBleDevicesList.remove(macAddress)
                }
              }
          }
      }

    compositeDisposable.add(connectTransmitterObservable.bind(serviceResult.onResult()))
    return compositeDisposable
  }

  private fun connectObservable(macAddress: String) : Observable<BluetoothServiceModel> {
    val stateMachine = UiStateMachineImpl()
    val overlayStateMachine = OverlayStateMachineImpl()

    return deviceConnection.subscribeForNotifications(macAddress)
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
            val activeTransmitter = transmitterManager.isActiveTransmitter(deviceResult.macAddress)
            BluetoothServiceModel.RawDataPackageUpdate(activeTransmitter, deviceResult.macAddress, deviceResult.rawDataPackage)
          }
          is DeviceResult.Error -> {
            // notify transmitter manager that this Tx was disconnected
            // if possible it will return the latest state change from the *other* Tx (used to update Ui)
            val latestStateChangeFromOtherTx = transmitterManager.onTransmitterDisconnect(macAddress)
            // return a disconnect to the service
            BluetoothServiceModel.Disconnected(latestStateChangeFromOtherTx)
          }
        }
      }
      .onErrorReturn { BluetoothServiceModel.Disconnected() }
  }
}