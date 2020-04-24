package com.coloplast.olympus.bluetooth

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.model.BluetoothServiceModel
import com.coloplast.olympus.bluetooth.model.BluetoothServiceResult
import com.coloplast.olympus.bluetooth.model.ServiceViewModel
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.feature.home.models.StateTransformers
import com.coloplast.olympus.ui.feature.statemachine.OverlayStateMachine
import com.coloplast.olympus.ui.feature.statemachine.OverlayStateMachineImpl
import com.coloplast.olympus.ui.feature.statemachine.UiStateMachine
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
  private val deviceConnection: DeviceConnection
) : ServiceViewModel<BluetoothServiceModel, BluetoothServiceResult<BluetoothServiceModel>>() {
  
  private val stateMachines = mutableMapOf<String, UiStateMachine>()
  private val overlayStateMachines = mutableMapOf<String, OverlayStateMachine>()
 
  private val storedBleDevicesWithDelay: Observable<Transmitter> = transmitterComponent.getTransmitters()
    .subscribeOn(Schedulers.io())
    .map {
      if (it is DataResult.Success<*>) {
        it.data as List<Transmitter>
      }
      else {
        emptyList()
      }
    }
    .doOnNext { Timber.d("transmitter count ${it.size}") }
    .filter { it.isNotEmpty() }
    .flatMapIterable { it }
  
  //Observable.fromArray("C3:40:E1:28:64:E9", "CA:6C:F7:03:1E:69")
  private val connectedBleDevices = mutableListOf<String>()
  
  // the repeatIntervalObservable to determine when to repeat something
  private fun repeatIntervalObservable(seconds: Long) = Observable.interval(seconds, TimeUnit.SECONDS)
  
  override fun bind(serviceResult: BluetoothServiceResult<BluetoothServiceModel>): Disposable {
    val compositeDisposable = CompositeDisposable()
    
    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    val bluetoothStateReadyObservable = currentBluetoothStateObservable.filter { it == BluetoothState.READY }
    
    val beaconEnteredRange = bluetoothStateReadyObservable
      .doOnNext { Timber.d("ready") }
      .switchMap {
        storedBleDevicesWithDelay
          .repeatWhen { repeatIntervalObservable(20) }
          .retryWhen { repeatIntervalObservable(20) }
          .zipWith(repeatIntervalObservable(5),
            BiFunction { transmitter: Transmitter, _: Long -> transmitter })
          .filter { transmitter -> connectedBleDevices.none { transmitter.macAddress == it } }
          .flatMap {
            Timber.d("Begin scan")
            bluetoothManager.scanSpecificBeacon(it.macAddress)
              // scan for 15 seconds only
              .takeUntil(repeatIntervalObservable(15).doOnNext { Timber.d("stopping scan") })
              .doOnNext { Timber.d("found beacon") }
          }
          .distinctUntilChanged()
      }
    
    
    val connectTransmitterObservable = beaconEnteredRange
      .doOnNext { Timber.d("trying to connect") }
      .flatMap { macAddress ->
        
        deviceConnection.subscribeForNotifications(macAddress)
          .doOnNext { connectedBleDevices.add(macAddress) }
          .doOnError {
            connectedBleDevices.remove(macAddress)
          }
          .doOnComplete {
            connectedBleDevices.remove(macAddress)
          }
          .map { deviceResult ->
            when (deviceResult) {
              is DeviceResult.DeviceUpdateResult -> {
                // we have a statemachine for each connected transmitter so either create a new one or use the one we already have
                val stateMachine = stateMachines[macAddress] ?: UiStateMachineImpl().also { stateMachines[macAddress] = it }
                val overlayStateMachine = overlayStateMachines[macAddress] ?: OverlayStateMachineImpl().also { overlayStateMachines[macAddress] = it }
        
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
                BluetoothServiceModel.Disconnected()
              }
            }
          }
      }
    
    compositeDisposable.add(connectTransmitterObservable.bind(serviceResult.onResult()))
    return compositeDisposable
  }
}