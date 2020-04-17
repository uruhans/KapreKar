package com.coloplast.olympus.bluetooth

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.model.BluetoothServiceModel
import com.coloplast.olympus.bluetooth.model.BluetoothServiceResult
import com.coloplast.olympus.bluetooth.model.ServiceViewModel
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
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
import timber.log.Timber
import java.util.concurrent.TimeUnit

class BluetoothServiceViewModel(
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent,
  private val deviceConnectionManager: DeviceConnectionManager
) : ServiceViewModel<BluetoothServiceModel, BluetoothServiceResult<BluetoothServiceModel>>() {
  
  private val stateMachines = mutableMapOf<String, UiStateMachine>()
  private val overlayStateMachines = mutableMapOf<String, OverlayStateMachine>()
  
  private val listener: DeviceResultListener by lazy {
    DeviceResultListener()
  }
  
  // TODO - to be replaced by database call once we can add transmitters to database
  private val storedBleDevices = Observable.fromArray("C3:40:E1:28:64:E9", "CA:6C:F7:03:1E:69")
  private val connectedBleDevices = mutableListOf<String>()
  
  // the repeatIntervalObservable to determine when to repeat something
  private fun repeatIntervalObservable(seconds: Long) = Observable.interval(seconds, TimeUnit.SECONDS)
  
  private val transmitterResultRelay = PublishRelay.create<BluetoothServiceModel>()
  
  override fun bind(serviceResult: BluetoothServiceResult<BluetoothServiceModel>): Disposable {
    val compositeDisposable = CompositeDisposable()
    
    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    
    val bluetoothReadyObservable = currentBluetoothStateObservable
      .filter { it == BluetoothState.READY }
    
    val beaconEnteredRange = bluetoothReadyObservable
      .flatMap {
        storedBleDevices
          .zipWith(repeatIntervalObservable(5),
            BiFunction { macAddress: String, _: Long -> macAddress }
          )
          .repeatWhen { repeatIntervalObservable(60) }
      }
      .filter { macAddress -> connectedBleDevices.none { macAddress == it } }
      .doOnNext { Timber.d(it) }
      .flatMap { deviceAddress ->
        bluetoothManager.scanSpecificBeacon(deviceAddress)
          // scan for 5 seconds only
          .takeUntil(repeatIntervalObservable(5))
          .doOnNext { Timber.d("Found beacon: $it") }
      }
      // retryWhen happens when the observables onError hits
      .retryWhen { repeatIntervalObservable(60) }
      // repeatWhen happens when the observables onComplete hits
      .repeatWhen { repeatIntervalObservable(60) }
      .distinctUntilChanged()
    
    val transmitterResultObservable = transmitterResultRelay.publish().refCount()
    
//    val transmitterResultObservable = Observable.create<BluetoothServiceModel> { emitter ->
//      listener.emitter = emitter
//    }
//      .publish()
//      .refCount()
    
    val connectTransmitterObservable = beaconEnteredRange
      .flatMap { macAddress ->
        deviceConnectionManager.connect(macAddress, listener)
        transmitterResultObservable
          .takeUntil(
            bluetoothManager.bleDisconnectedObservable(macAddress)
              .doOnNext { connectedBleDevices.remove(macAddress) }
              .skip(1)
          )
          .doOnNext { connectedBleDevices.add(macAddress) }
          .doOnError {
            Timber.e("$it")
            //listener.emitter = null
          }
          .doOnComplete { /*listener.emitter = null*/ }
      }
    
    
//    val connectObservable = Observable.create<BluetoothServiceModel> { emitter ->
//      listener.emitter = emitter
//    }
//
//    beaconEnteredRange
//      .flatMap { macAddress ->
//        // connect to the beacon that has just entered out range
//        deviceConnectionManager.connect(macAddress, listener)
//        connectObservable
//      }
//      .doOnError {
//        Timber.e("$it")
//        listener.emitter = null
//      }
//      //        .onErrorReturn {
//      //          emitter.onError(it)
//      //          listener.emitter = null
//      //        }
//      .doOnComplete { listener.emitter = null }
//      .subscribe()
//
    compositeDisposable.add(connectTransmitterObservable.bind(serviceResult.onResult()))
    return compositeDisposable
  }
  
  inner class DeviceResultListener : DeviceConnectionManager.Listener {
    //var emitter: ObservableEmitter<BluetoothServiceModel>? = null
    
    override fun onResult(deviceResult: DeviceResult) {
      val result = when (deviceResult) {
        is DeviceResult.DeviceUpdateResult -> {
          val macAddress = deviceResult.bleDevice.macAddress
          
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
      
      transmitterResultRelay.accept(result)
      
      
      //emitter?.onNext(uiModel)
    }
  }
}