package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.models.BluetoothState
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

class HomeViewModel(private val bluetoothManager: BluetoothManager) : BaseViewModel<HomeUiModel, HomeView>() {
  override fun bind(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()
    
    val bluetoothStateObservable: Observable<HomeUiModel> = bluetoothManager.bluetoothStateObservable()
        .doOnNext { Timber.d("BluetoothState: $it") }
        .filter { it != BluetoothState.READY }
        .map { HomeUiModel.BluetoothIssue(it) }
    
    disposable.add(bluetoothStateObservable.bind(uiView.render()))
    return disposable
  }
}