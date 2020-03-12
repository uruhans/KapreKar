package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.DeviceConnectionManager
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.ui.feature.home.models.BaseHomeViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import com.coloplast.olympus.ui.feature.home.models.Transformers
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
          
//          deviceConnectionManager.connect(macAddress, object : DeviceConnectionManager.Listener {
//            override fun onResult(deviceResult: DeviceResult) {
//              val uiModel = when (deviceResult) {
//                is DeviceResult.DeviceUpdateResult -> {
//                  val stateModel = Transformers.bleToUiState(deviceResult)
//                  val newUiState = stateMachine.invoke(stateModel.state)
//
//                  val overlayStateModel = Transformers.bleToOverlayState(deviceResult)
//                  val newOverlayState = overlayStateMachine.invoke(overlayStateModel.overlayState)
//                  HomeUiModel.StatusUpdate(newUiState, newOverlayState, deviceResult.bleDevice)
//                }
//                is DeviceResult.RawDataPackageResult -> {
//                  HomeUiModel.RawDataPackageUpdate(deviceResult.rawDataPackage)
//                }
//              }
//              emitter.onNext(uiModel)
//            }
//          })
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
          val stateModel = Transformers.bleToUiState(deviceResult)
          val newUiState = stateMachine.invoke(stateModel.state)
      
          val overlayStateModel = Transformers.bleToOverlayState(deviceResult)
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
  
  //  override fun connectBleDevice(uiView: HomeView): Disposable {
  //    val disposable = CompositeDisposable()
  //
  //    val bleDeviceConnectedObservable = uiView.onConnectPeripheral()
  //      .scan { previous: String, now: String ->
  //        if(previous.isNotEmpty()) {
  //          ""
  //        }
  //        else {
  //          now
  //        }
  //      }
  //      .doOnNext { Timber.d("macAddress: $it") }
  //      .filter { it.isNotEmpty() }
  //      // delay added based on issues with some devices that cannot immediately connect right after a scan
  //      // reference: https://github.com/Polidea/RxAndroidBle/wiki/FAQ:-Cannot-connect
  //      .delay(500, TimeUnit.MILLISECONDS)
  //      .switchMap { bluetoothManager.connectBleDevice(it) }
  //      .publish()
  //      .autoConnect()
  //
  //    val notificationSubscriptionObservable = bleDeviceConnectedObservable
  //      .flatMap { bluetoothManager.bleDeviceResultObservable(it) }
  //
  //    val uiStateObservable = notificationSubscriptionObservable
  //      .compose(BleToUiStateTransformer())
  //      .map {
  //        val newState = stateMachine.invoke(it.state)
  //        HomeUiModel.StatusUpdate(newState, bleDevice = it.bleDevice)
  //      }
  //
  //    val overlayStateObservable = notificationSubscriptionObservable
  //      .compose(BleToOverlayStateTransformer())
  //      .map {
  //        val newState = overlayStateMachine.invoke(it.overlayState)
  //        HomeUiModel.StatusUpdate(overlayState = newState, bleDevice = it.bleDevice)
  //      }
  //
  //    val rawDataPackageObservable = bleDeviceConnectedObservable
  //      .flatMap { bluetoothManager.setupOlympusRawDataPackageNotifications(it) }
  //      .map { HomeUiModel.RawDataPackageUpdate(it) as HomeUiModel }
  //      .onErrorReturn { HomeUiModel.Disconnected() }
  //
  //    val uiModelObservable =
  //      Observable.merge(
  //        uiStateObservable,
  //        overlayStateObservable,
  //        rawDataPackageObservable
  //      )
  //        .onErrorReturn {
  //          Timber.e(it)
  //          when (it) {
  //            is DisconnectException -> HomeUiModel.Disconnected()
  //            is GattCallbackTimeoutException -> HomeUiModel.GattError(HomeUiModel.GattError.State.TimeOutException)
  //            is GattException -> HomeUiModel.Disconnected()
  //            else -> HomeUiModel.Error()
  //          }
  //        }
  //
  //    disposable.add(uiModelObservable.bind(uiView.render()))
  //    return disposable
  //  }
}