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
  private val deviceConnectionManager: DeviceConnectionManager
) : ServiceViewModel<BluetoothServiceModel, BluetoothServiceResult<BluetoothServiceModel>>() {
  
  private val stateMachines = mutableMapOf<String, UiStateMachine>()
  private val overlayStateMachines = mutableMapOf<String, OverlayStateMachine>()
  
//  private val listener: DeviceResultListener by lazy {
//    DeviceResultListener()
//  }
  
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
  
  private val transmitterResultRelay = PublishRelay.create<BluetoothServiceModel>()
  
  override fun bind(serviceResult: BluetoothServiceResult<BluetoothServiceModel>): Disposable {
    val compositeDisposable = CompositeDisposable()
    
    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    val bluetoothStateReadyObservable = currentBluetoothStateObservable.filter { it == BluetoothState.READY }
    
    val beacon = bluetoothStateReadyObservable
      .doOnNext {
        deviceConnectionManager.cleanup()
        Timber.d("ready")
      }
      .flatMap {
        storedBleDevicesWithDelay
          .zipWith(repeatIntervalObservable(5),
            BiFunction { transmitter: Transmitter, _: Long -> transmitter })
          .repeatWhen { repeatIntervalObservable(20) }
          .retryWhen { repeatIntervalObservable(20) }
          .filter { transmitter -> connectedBleDevices.none { transmitter.macAddress == it } }
          .flatMap {
            Timber.d("Begin scan")
    
            bluetoothManager.scanSpecificBeacon(it.macAddress)
              // scan for 5 seconds only
              .takeUntil(repeatIntervalObservable(15).doOnNext { Timber.d("stopping scan") })
              .doOnNext { Timber.d("found beacon") }
            //          .repeatWhen { repeatIntervalObservable(30).doOnNext { Timber.d("repeating scan") } }
          }
          .distinctUntilChanged()
      }
    
    
    //    val beaconEnteredRange = //bluetoothStateReadyObservable
    //      //.flatMap {
    //        storedBleDevicesWithDelay
    //          .doOnNext { Timber.d("Transmitter with delay -> ${it.customName}") }
    //          .filter { transmitter -> connectedBleDevices.none { transmitter.macAddress == it } }
    //          .doOnNext { Timber.d(it.customName) }
    //      //}
    //      .repeatUntil { bluetoothManager.bluetoothStateObservable().map { it != BluetoothState.READY }.blockingFirst() }
    //      // retryWhen happens when the observables onError hits
    //      .retryWhen { repeatIntervalObservable(60) }
    //      // repeatWhen happens when the observables onComplete hits
    //      .repeatWhen { repeatIntervalObservable(60) }
    //      .doOnError { Timber.e(it) }
    //
    //      .flatMap { transmitter ->
    //        // start a short initial scan for the specific transmitter in order to connect to it
    //        bluetoothManager.scanSpecificBeacon(transmitter.macAddress)
    //          // scan for 5 seconds only
    //          .takeUntil(repeatIntervalObservable(5))
    //          .doOnNext { Timber.d("Found beacon: $it") }
    //          .doOnError { Timber.e(it) }
    //      }
    
    // skip the transmitter if it's already connected
    
    // val transmitterResultObservable = deviceConnectionManager.onResultObservable()
    
    val connectTransmitterObservable = beacon
      .doOnNext { Timber.d("trying to connect") }
      .flatMap { macAddress ->
        // TODO - it seems that this observable can have multiple instances on the same transmitter (i believe its because if connection fails in the beginning - it's not properly disposed of)
        deviceConnectionManager.connect(macAddress)
//          .takeUntil(
//            // only get results as long as the transmitter is connected
//            bluetoothManager.bleDisconnectedObservable(macAddress)
//              .doOnNext { connectedBleDevices.remove(macAddress) }
//              .doOnNext { Timber.d("$macAddress was disconnected [$it]") }
//              // TODO - verify that this is indeed needed
//              .skip(1)
//          )
          .doOnNext { connectedBleDevices.add(macAddress) }
          .doOnError { deviceConnectionManager.cleanup(macAddress) }
          .doOnComplete { deviceConnectionManager.cleanup(macAddress) }
          .map {
            // this is an attempt (perhaps unneeded - further testing required) to reduce the number of callbacks
            // what seems to be happening, is that we have two connected transmitter relays, and for each onNext (no matter which transmitter is sending info)
            // the relay is hit once for each subscription (ie: twice when two transmitters connected)
            // this will compare the incoming macAddress with the one in the result and only onNext if they match
            // TODO - verify this is needed, or find another way to get results from two transmitters at the same time
            if (it is DeviceResult.DeviceUpdateResult) {
              if (it.bleDevice.macAddress == macAddress) {
                return@map it
              }
            }
            else if (it is DeviceResult.RawDataPackageResult) {
              if (it.macAddress == macAddress) {
                return@map it
              }
            }
    
            return@map DeviceResult.Error()
          }
          .filter { it !is DeviceResult.Error }
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
      
    
    //        transmitterResultObservable
    //          .map {
    //            // this is an attempt (perhaps unneeded - further testing required) to reduce the number of callbacks
    //            // what seems to be happening, is that we have two connected transmitter relays, and for each onNext (no matter which transmitter is sending info)
    //            // the relay is hit once for each subscription (ie: twice when two transmitters connected)
    //            // this will compare the incoming macAddress with the one in the result and only onNext if they match
    //            // TODO - verify this is needed, or find another way to get results from two transmitters at the same time
    //            if (it is BluetoothServiceModel.DeviceUpdate) {
    //              if (it.statusChange.bleDevice.macAddress == macAddress) {
    //                return@map it
    //              }
    //            }
    //            else if (it is BluetoothServiceModel.RawDataPackageUpdate) {
    //              if (it.macAddress == macAddress) {
    //                return@map it
    //              }
    //            }
    //
    //            return@map BluetoothServiceModel.Ignore(macAddress)
    //          }
    //          .filter { it !is BluetoothServiceModel.Ignore }
    //          .takeUntil(
    //            // only get results as long as the transmitter is connected
    //            bluetoothManager.bleDisconnectedObservable(macAddress)
    //              .doOnNext { connectedBleDevices.remove(macAddress) }
    //              .doOnNext { Timber.d("$macAddress was disconnected [$it]") }
    //              // TODO - verify that this is indeed needed
    //              .skip(1)
    //          )
    //          .doOnNext { connectedBleDevices.add(macAddress) }
    //          .doOnError { deviceConnectionManager.cleanup(macAddress) }
    //          .doOnComplete { deviceConnectionManager.cleanup(macAddress) }
    //}
    
    compositeDisposable.add(connectTransmitterObservable.bind(serviceResult.onResult()))
    return compositeDisposable
  }
  
//  inner class DeviceResultListener : DeviceConnectionManager.Listener {
//    override fun onResult(deviceResult: DeviceResult) {
//      val result = when (deviceResult) {
//        is DeviceResult.DeviceUpdateResult -> {
//          val macAddress = deviceResult.bleDevice.macAddress
//
//          // we have a statemachine for each connected transmitter so either create a new one or use the one we already have
//          val stateMachine = stateMachines[macAddress] ?: UiStateMachineImpl().also { stateMachines[macAddress] = it }
//          val overlayStateMachine = overlayStateMachines[macAddress] ?: OverlayStateMachineImpl().also { overlayStateMachines[macAddress] = it }
//
//          val stateModel = StateTransformers.bleToUiState(deviceResult)
//          val newUiState = stateMachine.invoke(stateModel.state)
//
//          val overlayStateModel = StateTransformers.bleToOverlayState(deviceResult)
//          val newOverlayState = overlayStateMachine.invoke(overlayStateModel.overlayState)
//
//          BluetoothServiceModel.DeviceUpdate(StateChange(newUiState, newOverlayState, deviceResult.bleDevice))
//        }
//        is DeviceResult.RawDataPackageResult -> {
//          BluetoothServiceModel.RawDataPackageUpdate(deviceResult.macAddress, deviceResult.rawDataPackage)
//        }
//        is DeviceResult.Error -> {
//          BluetoothServiceModel.Disconnected()
//        }
//      }
//
//      transmitterResultRelay.accept(result)
//    }
//  }
}