package com.coloplast.olympus.ui.feature.transmitters.add.at9

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AddTransmitterAT9ViewModel(private val bluetoothManager: BluetoothManager,
                                 private val transmitterComponent: TransmitterComponent) :
  BaseViewModel<SearchTransmitterUiModel, AddTransmitterAT9View>() {

  // TODO - timeouts in this viewmodel might need to be changed (shorter)

  override fun bind(uiView: AddTransmitterAT9View): Disposable {
    val disposable = CompositeDisposable()

    val scanResultObservable: Observable<SearchTransmitterUiModel> =
      bluetoothManager.bluetoothStateObservable()
        // only scan if we have a bluetooth state that is ready (enabled and permissions are set)
        .filter { it == BluetoothState.READY }
        .flatMap {
          // scan for transmitters
          bluetoothManager.scanBleDevices()
            // only return distinct items (based on macAddress)
            .distinctUntilChanged { scanResult: ScanResult -> scanResult.macAddress }
            // shut down the scanning after 14 seconds
            // it's 14 (and not 15) because the buffer would stop a little bit before this
            // which resulted in the buffer being emptied twice (which caused issues in the UI)
            .takeUntil(Observable.interval(14, TimeUnit.SECONDS))
            // timeout after 15 seconds
            .timeout(15, TimeUnit.SECONDS)
            // buffer *all* results for 15 seconds
            // this allows the observable to onNext a list instead of single results
            .buffer(15, TimeUnit.SECONDS)
            .map {
              if(it.isEmpty()) {
                SearchTransmitterUiModel.NothingFound()
              }
              else {
                SearchTransmitterUiModel.TransmittersFound(it)
              }
            }
            .onErrorReturnItem(SearchTransmitterUiModel.NothingFound())
        }
        .startWith(SearchTransmitterUiModel.PendingUser())

    val addToDatabaseAndObservableConnectionStateObservable = uiView.scanResultToConnect()
      .switchMap { scanResult ->
        // insert the transmitter into the database (this will mean that the bluetoothservice
        // will pick up the change in DB and start connecting to it
        transmitterComponent.insertOrUpdateTransmitter(Transmitter(scanResult.macAddress, scanResult.name!!))
          .subscribeOn(Schedulers.io())
          .toSingle { true }
          .toObservable()
          .flatMap {
            // keep an eye out for the connection state of the transmitter
            bluetoothManager.bleConnectionStateObservable(scanResult.macAddress)
              // skip the first because it's always disconnected
              .skip(1)
              // we are only interested in fully connected or disconnected
              .filter { it == BleConnectionState.CONNECTED || it == BleConnectionState.DISCONNECTED }
              .flatMap {
                if(it == BleConnectionState.CONNECTED) {
                  // connection was successful - return and notify controller
                  Observable.just(SearchTransmitterUiModel.ConnectionSuccessful(scanResult.macAddress))
                    // we have a slight delay here, because if bonding actually fails
                    // we will hit CONNECTED first, followed by DISCONNECTED
                    .delay(2, TimeUnit.SECONDS)
                }
                else {
                  // connection was unsuccessful, so we remove the transmitter from the database again.
                  // this will prevent the bluetoothservice in picking it up and continuously connect and fail
                  transmitterComponent.deleteTransmitter(scanResult.macAddress)
                    .subscribeOn(Schedulers.io())
                    .toSingle { SearchTransmitterUiModel.ConnectionFailure(scanResult.macAddress) }
                    .toObservable()
                }
              }
          }
      }

    disposable.add(Observable.merge(scanResultObservable, addToDatabaseAndObservableConnectionStateObservable).bind(uiView.render()))
    return disposable
  }
}