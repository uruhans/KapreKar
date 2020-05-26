package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.ui.feature.home.models.BaseHomeViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

class HomeViewModel(private val bluetoothManager: BluetoothManager) :
    BaseHomeViewModel() {
  
  override fun bind(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    val bluetoothStateObservable: Observable<HomeUiModel> = currentBluetoothStateObservable
      .map<HomeUiModel> { HomeUiModel.BluetoothIssue(it) }
      .onErrorReturn {
        Timber.e(it)
        HomeUiModel.ConnectionStateUpdate(BleConnectionState.DISCONNECTED)
      }
    
    val stateUpdate = StateDispatcher.stateChangeObservable()
      .map { HomeUiModel.DeviceUpdate(it) }
    
    val rawPackageUpdateObservable = StateDispatcher.rawPackageUpdateObservable()
      .map { HomeUiModel.RawDataPackageUpdate(it) }

    val resetTransmitterObservable = uiView.requestReset()
      .switchMap { macAddress ->
        bluetoothManager.resetTransmitter(macAddress)
          .toSingle<HomeUiModel> { HomeUiModel.StitchOrResetComplete() }
          .toObservable()
      }

    val stitchTransmitterObservable = uiView.requestStitching()
      .switchMap { stitch ->
        bluetoothManager.stitchTransmitter(stitch.macAddress, stitch.dataPackage)
          .toSingle<HomeUiModel> { HomeUiModel.StitchOrResetComplete() }
          .toObservable()
      }

    disposable.add(Observable.merge(stateUpdate, bluetoothStateObservable, rawPackageUpdateObservable).bind(uiView.render()))
    disposable.add(Observable.merge(resetTransmitterObservable, stitchTransmitterObservable).bind(uiView.render()))

    return disposable
  }
  
  override fun bindConnectionState(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val connectionStateObservable = uiView.requestConnectionState()
      .switchMap { macAddress ->
        bluetoothManager.bleConnectionStateObservable(macAddress)
          .map<HomeUiModel> { HomeUiModel.ConnectionStateUpdate(it) }
      }
    
    disposable.add(connectionStateObservable.bind(uiView.render()))
    return disposable
  }
}