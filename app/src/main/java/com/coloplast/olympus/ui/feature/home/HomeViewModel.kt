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
import java.util.concurrent.TimeUnit

class HomeViewModel(private val bluetoothManager: BluetoothManager) : BaseHomeViewModel() {
  
  private val stateMachine: UiStateMachine = UiStateMachineImpl()
  private val overlayStateMachine: OverlayStateMachine = OverlayStateMachineImpl()
  
  override fun bind(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    val bluetoothStateObservable: Observable<HomeUiModel> = currentBluetoothStateObservable
      .map { HomeUiModel.BluetoothIssue(it) }
    
    disposable.add(bluetoothStateObservable.bind(uiView.render()))
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
              .map { HomeUiModel.ScannerResult(it) }
          }
      }
    
    disposable.add(scanResultObservable.bind(uiView.render()))
    return disposable
  }
  
  override fun connectBleDevice(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val peripheralObservable = uiView.onConnectPeripheral()
      // delay added based on issues with some devices that cannot immediately connect right after a scan
      // reference: https://github.com/Polidea/RxAndroidBle/wiki/FAQ:-Cannot-connect
      .delay(500, TimeUnit.MILLISECONDS)
      .switchMap { macAddress ->
        bluetoothManager.connectBleDevice(macAddress)
      }
      .publish()
      .autoConnect()
    
    val uiStateObservable = peripheralObservable
      .compose(BleToUiStateTransformer())
      .map {
        val newState = stateMachine.invoke(it.uiState)
        HomeUiModel.StatusUpdate(newState, it.bleDevice) as HomeUiModel
      }
    
    val overlayStateObservable = peripheralObservable
      .compose(BleToOverlayStateTransformer())
      .map {
        val newState = overlayStateMachine.invoke(it.overlayState)
        HomeUiModel.OverlayUpdate(newState, it.bleDevice)
      }
    
    val uiModelObservable =
      Observable.merge(
        uiStateObservable,
        overlayStateObservable
      )
        .onErrorReturn {
          when (it) {
            is DisconnectException -> HomeUiModel.Disconnected()
            is GattCallbackTimeoutException -> HomeUiModel.GattError(HomeUiModel.GattError.State.TimeOutException)
            is GattException -> HomeUiModel.GattError()
            else -> HomeUiModel.Error()
          }
        }
    
    disposable.add(uiModelObservable.bind(uiView.render()))
    return disposable
  }
}