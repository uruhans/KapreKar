package com.coloplast.olympus.ui.feature.transmitters.add.at7

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.at7.model.AddTransmitterAT7UiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AddTransmitterAT7ViewModel(private val bluetoothManager: BluetoothManager) :
  BaseViewModel<AddTransmitterAT7UiModel, AddTransmitterAT7View>() {
  override fun bind(uiView: AddTransmitterAT7View): Disposable {
    val disposable = CompositeDisposable()

    val bluetoothStateObservable: Observable<AddTransmitterAT7UiModel> =
      bluetoothManager.bluetoothStateObservable()
        .map { AddTransmitterAT7UiModel(it) }

    disposable.add(bluetoothStateObservable.bind(uiView.render()))
    return disposable
  }
}