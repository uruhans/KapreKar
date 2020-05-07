package com.coloplast.olympus.ui.feature.transmitters.add.at9

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class AddTransmitterAT9ViewModel(private val bluetoothManager: BluetoothManager) :
  BaseViewModel<SearchTransmitterUiModel, AddTransmitterAT9View>() {

  override fun bind(uiView: AddTransmitterAT9View): Disposable {
    val disposable = CompositeDisposable()

    // the repeatIntervalObservable to determine when to repeat something
    val repeatIntervalObservable = Observable.interval(14, TimeUnit.SECONDS)

    val scanResultObservable: Observable<SearchTransmitterUiModel> =
      bluetoothManager.bluetoothStateObservable()
        .filter { it == BluetoothState.READY }
        .flatMap {
          //uiView.getTransmitterName()
          // TODO - add name to filter or specific transmitter to scan for
          bluetoothManager.scanBleDevices()
            .takeUntil(repeatIntervalObservable)
            .timeout(15, TimeUnit.SECONDS)
            .buffer(15, TimeUnit.SECONDS)
            .map { SearchTransmitterUiModel.TransmittersFound(it) as SearchTransmitterUiModel }
            .onErrorReturnItem(SearchTransmitterUiModel.NothingFound())
        }
        .startWith(SearchTransmitterUiModel.PendingUser())
    
    disposable.add(scanResultObservable.bind(uiView.render()))
    return disposable
  }
}