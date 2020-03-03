package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.exceptions.DisconnectException
import com.coloplast.olympus.bluetooth.businesslayer.exceptions.GattCallbackTimeoutException
import com.coloplast.olympus.bluetooth.businesslayer.exceptions.GattException
import com.coloplast.olympus.ui.feature.home.models.BaseHomeViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import com.coloplast.olympus.ui.feature.home.rx.BleToOverlayStateTransformer
import com.coloplast.olympus.ui.feature.home.rx.BleToUiStateTransformer
import com.coloplast.olympus.ui.feature.statemachine.OverlayStateMachine
import com.coloplast.olympus.ui.feature.statemachine.OverlayStateMachineImpl
import com.coloplast.olympus.ui.feature.statemachine.UiStateMachine
import com.coloplast.olympus.ui.feature.statemachine.UiStateMachineImpl
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class HomeViewModel(private val bluetoothManager: BluetoothManager) : BaseHomeViewModel() {
  
  private lateinit var stateMachine: UiStateMachine
  private lateinit var overlayStateMachine: OverlayStateMachine
  
  override fun bind(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    val bluetoothStateObservable: Observable<HomeUiModel> = currentBluetoothStateObservable
      .map { HomeUiModel.BluetoothIssue(it) as HomeUiModel }
      .onErrorReturn {
        Timber.e(it)
        HomeUiModel.Disconnected()
      }
    
    disposable.add(bluetoothStateObservable.bind(uiView.render()))
    return disposable
  }
  
  override fun bindScanner(uiView: HomeView): Disposable {
    stateMachine = UiStateMachineImpl()
    overlayStateMachine = OverlayStateMachineImpl()
    
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
    
    val bleDeviceConnectedObservable = uiView.onConnectPeripheral()
      // delay added based on issues with some devices that cannot immediately connect right after a scan
      // reference: https://github.com/Polidea/RxAndroidBle/wiki/FAQ:-Cannot-connect
      .delay(500, TimeUnit.MILLISECONDS)
      .switchMap { bluetoothManager.connectBleDevice(it) }
      .publish()
      .autoConnect()
    
    val notificationSubscriptionObservable = bleDeviceConnectedObservable
      .flatMap { bluetoothManager.bleDeviceResultObservable(it) }
    
    val uiStateObservable = notificationSubscriptionObservable
      .compose(BleToUiStateTransformer())
      .map {
        val newState = stateMachine.invoke(it.state)
        HomeUiModel.StatusUpdate(newState, bleDevice = it.bleDevice)
      }
    
    val overlayStateObservable = notificationSubscriptionObservable
      .compose(BleToOverlayStateTransformer())
      .map {
        val newState = overlayStateMachine.invoke(it.overlayState)
        HomeUiModel.StatusUpdate(overlayState = newState, bleDevice = it.bleDevice)
      }
    
    val rawDataPackageObservable = bleDeviceConnectedObservable
      .flatMap { bluetoothManager.setupOlympusRawDataPackageNotifications(it) }
      .map { HomeUiModel.RawDataPackageUpdate(it) as HomeUiModel }
      .onErrorReturn { HomeUiModel.Disconnected() }
    
    val uiModelObservable =
      Observable.merge(
        uiStateObservable,
        overlayStateObservable,
        rawDataPackageObservable
      )
        .onErrorReturn {
          Timber.e(it)
          when (it) {
            is DisconnectException -> HomeUiModel.Disconnected()
            is GattCallbackTimeoutException -> HomeUiModel.GattError(HomeUiModel.GattError.State.TimeOutException)
            is GattException -> HomeUiModel.Disconnected()
            else -> HomeUiModel.Error()
          }
        }
    
    disposable.add(uiModelObservable.bind(uiView.render()))
    return disposable
  }
}