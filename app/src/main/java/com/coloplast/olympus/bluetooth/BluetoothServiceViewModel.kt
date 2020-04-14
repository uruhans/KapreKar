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
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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
  
  // TODO - to be replaced by database call
  private val storedBleDevices = Observable.fromArray("CA:6C:F7:03:1E:69", "C3:40:E1:28:64:E9")

  private fun repeatIntervalObservable(seconds: Long) = Observable.interval(seconds, TimeUnit.SECONDS)
  
  override fun bind(serviceResult: BluetoothServiceResult<BluetoothServiceModel>): Disposable {
    val compositeDisposable = CompositeDisposable()
  
    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    val bluetoothStateObservable = currentBluetoothStateObservable
      .filter { it == BluetoothState.READY }
    
    val beaconEnteredRange = bluetoothStateObservable
      .flatMap {
        bluetoothManager.scanBeacons()
          .takeUntil(repeatIntervalObservable(5))
          .doOnNext { Timber.d(it) }
          .retryWhen { repeatIntervalObservable(60) }
          .repeatWhen { repeatIntervalObservable(60) }
          .doOnNext { Timber.d("Found beacon: $it") }
          .flatMap { macAddress -> storedBleDevices.filter { macAddress == it } }
          .doOnNext { Timber.d("macAddress is in our database list - continue") }
      }
    
    val connectObservable = Observable.create<BluetoothServiceModel> { emitter ->
      listener.emitter = emitter
      
      beaconEnteredRange
        .map { macAddress ->
          deviceConnectionManager.connect(macAddress, listener)
        }
        .doOnError {
          Timber.e("$it")
          listener.emitter = null
        }
        //        .onErrorReturn {
        //          emitter.onError(it)
        //          listener.emitter = null
        //        }
        .doOnComplete { listener.emitter = null }
        .subscribe()
    }
    
    compositeDisposable.add(connectObservable.bind(serviceResult.onResult()))
    return compositeDisposable
  }
  
  inner class DeviceResultListener : DeviceConnectionManager.Listener {
    var emitter: ObservableEmitter<BluetoothServiceModel>? = null
    
    override fun onResult(deviceResult: DeviceResult) {
      val uiModel = when (deviceResult) {
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
      
      emitter?.onNext(uiModel)
    }
  }
}