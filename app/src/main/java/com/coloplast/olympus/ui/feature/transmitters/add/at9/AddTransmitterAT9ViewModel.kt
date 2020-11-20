package com.coloplast.olympus.ui.feature.transmitters.add.at9

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AddTransmitterAT9ViewModel(
  private val me: Me,
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent
) :
  BaseViewModel<SearchTransmitterUiModel, AddTransmitterAT9View>() {

  override fun bind(uiView: AddTransmitterAT9View): Disposable {
    val disposable = CompositeDisposable()

    val scanResultObservable: Observable<SearchTransmitterUiModel> =
      bluetoothManager.bluetoothStateObservable()
        // only scan if we have a bluetooth state that is ready (enabled and permissions are set)
        .filter { it == BluetoothState.READY }
        .flatMap {
          // scan for transmitters
          bluetoothManager.scanBleDevices()
            .doOnNext { Timber.d("ScanResult happened") }
            // only return distinct items (based on macAddress)
            .distinct { scanResult: ScanResult -> scanResult.macAddress }
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
              if (it.isEmpty()) {
                SearchTransmitterUiModel.NothingFound()
              }
              else {
                SearchTransmitterUiModel.TransmittersFound(it)
              }
            }
            .onErrorReturnItem(SearchTransmitterUiModel.NothingFound())
        }
        .startWith(SearchTransmitterUiModel.PendingUser())

    val addToDatabaseAndConnectionStateObservable = uiView.scanResultToConnect()
      .switchMap { scanResult ->

        me.currentUserObservable()
          .filter { it is CurrentUser.LoggedIn }
          .map { it as CurrentUser.LoggedIn }
          .map {

            val transmitterName = it.user.firstName?.let { firstName ->
              "$firstName's transmitter"
            }

            Transmitter(
              scanResult.macAddress,
              it.user.id!!,
              scanResult.getNameOrAddress(),
              transmitterName ?: scanResult.macAddress
            )
          }
          .flatMap { transmitter ->
            transmitterComponent.insertOrUpdateTransmitter(transmitter)
              .subscribeOn(Schedulers.io())
              .toSingle { true }
              .toObservable()
              .flatMap {
                // keep an eye out for the connection state of the transmitter
                bluetoothManager.bleConnectionStateObservable(scanResult.macAddress)
                  .skipWhile { it == BleConnectionState.DISCONNECTED }
                  // we are only interested in fully connected
                  .filter { it == BleConnectionState.CONNECTED }
                  .map { SearchTransmitterUiModel.ConnectionSuccessful(scanResult.macAddress) }
                  .takeUntil(
                    Observable.timer(20, TimeUnit.SECONDS)
                      .flatMap {
                        transmitterComponent.deleteTransmitter(scanResult.macAddress)
                          .subscribeOn(Schedulers.io())
                          .toSingle { SearchTransmitterUiModel.BondingFailed(scanResult.macAddress) }
                          .toObservable()
                      }
                  )
              }
          }
      }

    val retryScanningObservable = uiView.retryTransmitterSearchObservable()
      .switchMap { scanResultObservable }

    disposable.add(
      Observable.merge(
        scanResultObservable,
        retryScanningObservable,
        addToDatabaseAndConnectionStateObservable
      ).bind(uiView.render())
    )

    return disposable
  }
}