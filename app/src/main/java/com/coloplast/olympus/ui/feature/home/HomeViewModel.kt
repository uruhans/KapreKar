package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.DeviceConnectionManager
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.ui.feature.home.models.BaseHomeViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
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

class HomeViewModel(private val bluetoothManager: BluetoothManager, private val deviceConnectionManager: DeviceConnectionManager) :
    BaseHomeViewModel() {
  
  private val stateMachine: UiStateMachine by lazy {
    UiStateMachineImpl()
  }
  private val overlayStateMachine: OverlayStateMachine by lazy {
    OverlayStateMachineImpl()
  }
  
  private val listener: DeviceResultListener by lazy {
    DeviceResultListener()
  }
  
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
    
    disposable.add(
      bluetoothStateObservable.bind(uiView.render())
    )
    return disposable
  }
  
  override fun bindScanner(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val scanResultObservable: Observable<HomeUiModel> = uiView.onScanRequested()
      .switchMap {
        bluetoothManager.bluetoothStateObservable()
          .filter { it == BluetoothState.READY }
          .flatMap {
            bluetoothManager.scanBleDevices()
              .map { HomeUiModel.ScannerResult(it) as HomeUiModel }
          }
          .onErrorReturn {
            Timber.e(it)
            HomeUiModel.Disconnected()
          }
      }
    
    disposable.add(scanResultObservable.bind(uiView.render()))
    return disposable
  }
  
  override fun connectBleDevice(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val uiModelObservable = Observable.create<HomeUiModel> { emitter ->
      listener.emitter = emitter
      
      uiView.onConnectPeripheral()
        .map { macAddress ->
          deviceConnectionManager.connect(macAddress, listener)
        }
        .onErrorReturn {
          emitter.onError(it)
          listener.emitter = null
        }
        .doOnComplete { listener.emitter = null }
        .subscribe()
    }
    
    disposable.add(uiModelObservable.bind(uiView.render()))
    return disposable
  }
  
  inner class DeviceResultListener : DeviceConnectionManager.Listener {
    var emitter: ObservableEmitter<HomeUiModel>? = null
    
    override fun onResult(deviceResult: DeviceResult) {
      val uiModel = when (deviceResult) {
        is DeviceResult.DeviceUpdateResult -> {
          val stateModel = StateTransformers.bleToUiState(deviceResult)
          val newUiState = stateMachine.invoke(stateModel.state)
      
          val overlayStateModel = StateTransformers.bleToOverlayState(deviceResult)
          val newOverlayState = overlayStateMachine.invoke(overlayStateModel.overlayState)
          HomeUiModel.StatusUpdate(newUiState, newOverlayState, deviceResult.bleDevice)
        }
        is DeviceResult.RawDataPackageResult -> {
          HomeUiModel.RawDataPackageUpdate(deviceResult.rawDataPackage)
        }
      }
      
      emitter?.onNext(uiModel)
    }
  }
}