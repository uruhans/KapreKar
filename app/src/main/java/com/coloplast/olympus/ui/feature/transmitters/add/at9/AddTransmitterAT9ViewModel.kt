package com.coloplast.olympus.ui.feature.transmitters.add.at9

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.bluetooth.ServiceDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.bluetooth.model.ServiceEvent
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.TransmitterConnectionFailureException
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

    val bluetoothStateReadyObservable = bluetoothManager.bluetoothStateObservable()
      // only scan if we have a bluetooth state that is ready (enabled and permissions are set)
      .filter { it == BluetoothState.READY }

    val scanResultObservable = bluetoothStateReadyObservable
      .flatMap {
        bluetoothManager.scanBleDevices()
          .doOnNext { Timber.d("ScanResult happened") }
          // only return distinct items (based on macAddress)
          .distinct { scanResult: ScanResult -> scanResult.macAddress }
          // shut down the scanning after 14 seconds
          // it's 5 (and not 6) because the buffer would stop a little bit before this
          // which resulted in the buffer being emptied twice (which caused issues in the UI)
          .takeUntil(Observable.interval(5, TimeUnit.SECONDS))
          // timeout after 6 seconds
          .timeout(6, TimeUnit.SECONDS)
          // buffer *all* results for 6 seconds
          // this allows the observable to onNext a list instead of single results
          .buffer(6, TimeUnit.SECONDS)
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

    val addToDatabaseAndConnectionStateObservable = uiView.scanResultToConnect()
      .doOnNext { ServiceDispatcher.dispatchEvent(ServiceEvent.ReconnectionAttempt(true)) }
      .switchMap { scanResult ->

        me.currentUserObservable()
          .ofType(CurrentUser.LoggedIn::class.java)
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
                  .delay(3, TimeUnit.SECONDS)
                  .map<SearchTransmitterUiModel> { SearchTransmitterUiModel.ConnectionSuccessful(scanResult.macAddress) }
                  // if a connection has not happened within a 30 second window, we assume something is wrong
                  .takeUntil(Observable.timer(60, TimeUnit.SECONDS)
                    .flatMap {
                      // we therefore delete the transmitter from the database again
                      transmitterComponent.deleteTransmitter(scanResult.macAddress)
                        // convert the completable into an observable
                        .toSingle { false }
                        .toObservable()
                        // and throw exception
                        .map { throw TransmitterConnectionFailureException(scanResult.macAddress) }
                    })
                  // exception is caught here and a nothing found is returned (allowing the user to retry the search)
                  .onErrorReturn { SearchTransmitterUiModel.NothingFound() }
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