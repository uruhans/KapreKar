package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.DeviceConnectionManager
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.ui.feature.home.models.BaseHomeViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

class HomeViewModel(private val bluetoothManager: BluetoothManager) :
    BaseHomeViewModel() {
  
//  private val stateMachines = mutableMapOf<String, UiStateMachine>()
//  private val overlayStateMachines = mutableMapOf<String, OverlayStateMachine>()
//
//  private val listener: DeviceResultListener by lazy {
//    DeviceResultListener()
//  }
  
  override fun bind(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    val bluetoothStateObservable: Observable<HomeUiModel> = currentBluetoothStateObservable
      .map { HomeUiModel.BluetoothIssue(it) as HomeUiModel }
      .onErrorReturn {
        Timber.e(it)
        HomeUiModel.Disconnected()
      }
    
    //    val connectedDevices = bluetoothManager.connectedDevicesObservable()
    //      .map { HomeUiModel.ConnectedDevicesUpdate(it) }
    
    val stateUpdate = StateDispatcher.stateChangeObservable()
      .map { HomeUiModel.DeviceUpdate(it) }
    
    disposable.add(Observable.merge(stateUpdate,bluetoothStateObservable).bind(uiView.render()))
    return disposable
  }
  
// TODO - need to properly test the changes i made before removing the below commented lines

//  override fun bindScanner(uiView: HomeView): Disposable {
//    val disposable = CompositeDisposable()
//
//    val scanResultObservable: Observable<HomeUiModel> = uiView.onScanRequested()
//      .switchMap {
//        bluetoothManager.bluetoothStateObservable()
//          .filter { it == BluetoothState.READY }
//          .flatMap {
//            bluetoothManager.scanBleDevices()
//              .map { HomeUiModel.ScannerResult(it) as HomeUiModel }
//          }
//          .onErrorReturn {
//            Timber.e(it)
//            HomeUiModel.Disconnected()
//          }
//      }
//
//    disposable.add(scanResultObservable.bind(uiView.render()))
//    return disposable
//  }
  
//  override fun connectBleDevice(uiView: HomeView): Disposable {
//    val disposable = CompositeDisposable()
//
//    val uiModelObservable = Observable.create<HomeUiModel> { emitter ->
//      listener.emitter = emitter
//
//      uiView.onConnectPeripheral()
//        .map { macAddress ->
//          deviceConnectionManager.connect(macAddress, listener)
//          macAddress
//        }
//        .flatMapSingle { macAddress ->
//          transmitterComponent.insertOrUpdateTransmitter(Transmitter(macAddress, macAddress))
//            .subscribeOn(Schedulers.io())
//            .toSingle { macAddress }
//        }
//        .doOnError {
//          Timber.e("$it")
//          listener.emitter = null
//          ""
//        }
//        .onErrorReturn {
//          emitter.onError(it)
//          listener.emitter = null
//        }
//        .doOnComplete { listener.emitter = null }
//        .subscribe()
//    }
//
//    disposable.add(uiModelObservable.bind(uiView.render()))
//    return disposable
//  }
  
//  inner class DeviceResultListener : DeviceConnectionManager.Listener {
//    var emitter: ObservableEmitter<HomeUiModel>? = null
//
//    override fun onResult(deviceResult: DeviceResult) {
//      val uiModel = when (deviceResult) {
//        is DeviceResult.DeviceUpdateResult -> {
//          val macAddress = deviceResult.bleDevice.macAddress
//
//          val stateMachine = stateMachines[macAddress] ?: UiStateMachineImpl().also { stateMachines[macAddress] = it }
//          val overlayStateMachine = overlayStateMachines[macAddress] ?: OverlayStateMachineImpl().also { overlayStateMachines[macAddress] = it }
//
//          val stateModel = StateTransformers.bleToUiState(deviceResult)
//          val newUiState = stateMachine.invoke(stateModel.state)
//
//          val overlayStateModel = StateTransformers.bleToOverlayState(deviceResult)
//          val newOverlayState = overlayStateMachine.invoke(overlayStateModel.overlayState)
//          HomeUiModel.StatusUpdate(newUiState, newOverlayState, deviceResult.bleDevice)
//        }
//        is DeviceResult.RawDataPackageResult -> {
//          HomeUiModel.RawDataPackageUpdate(deviceResult.macAddress, deviceResult.rawDataPackage)
//        }
//        is DeviceResult.Error -> {
//          HomeUiModel.Disconnected()
//        }
//      }
//
//      emitter?.onNext(uiModel)
//    }
//  }
}