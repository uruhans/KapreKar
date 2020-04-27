package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
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
      .map { HomeUiModel.BluetoothIssue(it) as HomeUiModel }
      .onErrorReturn {
        Timber.e(it)
        HomeUiModel.Disconnected()
      }
    
    val stateUpdate = StateDispatcher.stateChangeObservable()
      .map { HomeUiModel.DeviceUpdate(it) }
    
    disposable.add(Observable.merge(stateUpdate,bluetoothStateObservable).bind(uiView.render()))
    return disposable
  }
}