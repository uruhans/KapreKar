package com.coloplast.olympus.ui.feature.transmitters

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.transmitters.models.TransmittersUiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class TransmittersViewModel(private val bluetoothManager: BluetoothManager) : BaseViewModel<TransmittersUiModel, TransmittersView>() {
  override fun bind(uiView: TransmittersView): Disposable {
    val disposable = CompositeDisposable()
    
    val connectedDevices = bluetoothManager.connectedDevicesObservable()
      .map<TransmittersUiModel> { TransmittersUiModel.ConnectedDevices(it) }
  
    disposable.add(connectedDevices.bind(uiView.render()))
    return disposable
  }
}