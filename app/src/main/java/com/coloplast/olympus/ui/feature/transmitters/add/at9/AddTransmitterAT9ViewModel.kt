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
    val intervalObservable = Observable.interval(14, TimeUnit.SECONDS)

    val scanResultObservable: Observable<SearchTransmitterUiModel> =
      bluetoothManager.bluetoothStateObservable()
        .filter { it == BluetoothState.READY }
        .flatMap {
          //uiView.getTransmitterName()
          // TODO - add name to filter or specific transmitter to scan for
          bluetoothManager.scanBleDevices()
            // TODO - add filter to test "nothing result"
            // .filter { it.macAddress == "sdf" }
            .takeUntil(intervalObservable)
            .timeout(15, TimeUnit.SECONDS)
            .buffer(15, TimeUnit.SECONDS)
            .map {
              return@map if (it.isEmpty()) {
                SearchTransmitterUiModel.NothingFound()
              } else {
                SearchTransmitterUiModel.TransmittersFound(it)
              }
            }
            .onErrorReturnItem(SearchTransmitterUiModel.NothingFound())
        }
        .startWith(SearchTransmitterUiModel.PendingUser())

    val retryScanningObservable = uiView.retryTransmitterSearchObservable()
      .switchMap { scanResultObservable }

    disposable.add(
      Observable.merge(scanResultObservable, retryScanningObservable).bind(uiView.render())
    )
    return disposable
  }
}