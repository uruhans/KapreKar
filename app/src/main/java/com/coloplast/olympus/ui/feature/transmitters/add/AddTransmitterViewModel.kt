package com.coloplast.olympus.ui.feature.transmitters.add

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AddTransmitterViewModel(private val bluetoothManager: BluetoothManager, private val transmitterComponent: TransmitterComponent) : BaseViewModel<GenericUiModel, AddTransmitterView>() {
  
  override fun bind(uiView: AddTransmitterView): Disposable {
    val disposable = CompositeDisposable()
  
    val scanResultObservable: Observable<GenericUiModel> = uiView.onScanRequested()
      .switchMap {
        bluetoothManager.bluetoothStateObservable()
          .filter { it == BluetoothState.READY }
          .flatMap {
            bluetoothManager.scanBleDevices()
              .map { GenericUiModel.Success(it) as GenericUiModel }
          }
          .onErrorReturn {
            Timber.e(it)
            GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 2020)
          }
      }
  
    val addTransmitterObservable = uiView.onAddTransmitter()
      .switchMap {
        transmitterComponent.insertOrUpdateTransmitter(Transmitter(it.macAddress, it.getNameOrAddress()))
          .subscribeOn(Schedulers.io())
          .toSingle { GenericUiModel.Success(true) }
          .toObservable()
      }
    
    disposable.add(Observable.merge(addTransmitterObservable, scanResultObservable).bind(uiView.render()))
    return disposable
  }
  
}