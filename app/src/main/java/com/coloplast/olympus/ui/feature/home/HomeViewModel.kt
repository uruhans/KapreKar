package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.models.BluetoothState
import com.coloplast.olympus.ui.feature.home.models.BaseHomeViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class HomeViewModel(private val bluetoothManager: BluetoothManager) : BaseHomeViewModel() {
  override fun bind(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
        .doOnNext { Timber.d("BluetoothState: $it") }
    
    val bluetoothStateObservable: Observable<HomeUiModel> = currentBluetoothStateObservable
        .map { HomeUiModel.BluetoothIssue(it) }
    
    val peripheralObservable =
      uiView.onConnectPeripheral()
          .switchMap { macAddress ->
            bluetoothManager.connectBleDevice(macAddress)
                .map { HomeUiModel.StatusUpdate(it, LocalDateTime.now()) }
          }
    
    disposable.add(Observable.merge(bluetoothStateObservable, peripheralObservable).bind(uiView.render()))
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
                    .map { HomeUiModel.Result(it) }
              }
        }
    
    disposable.add(scanResultObservable.bind(uiView.render()))
    return disposable
  }
}