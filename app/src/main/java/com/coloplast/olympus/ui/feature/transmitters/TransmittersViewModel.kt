package com.coloplast.olympus.ui.feature.transmitters

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.ServiceDispatcher
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.model.ConnectionState
import com.coloplast.olympus.bluetooth.model.ServiceEvent
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.transmitters.models.TransmittersUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TransmittersViewModel(
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent
) : BaseViewModel<TransmittersUiModel, TransmittersView>() {

  override fun bind(uiView: TransmittersView): Disposable {
    val disposable = CompositeDisposable()

    // transmitters stored in database
    val storedTransmitters = transmitterComponent.getTransmitters()
      .subscribeOn(Schedulers.io())
      .doOnNext { Timber.d("getTransmitters hit") }
      .distinctUntilChanged()

    val transmittersOrEmptyObservable = storedTransmitters
      .map {
        if (it is DataResult.Success<*>) {
          it.data as List<Transmitter>
        }
        else {
          emptyList()
        }
      }

    // transforming the stored transmitters into UiModel to be displayed on the screen
    val transformedTransmitters = transmittersOrEmptyObservable
      .map {
        if (it.isEmpty()) {
          TransmittersUiModel.Empty()
        }
        else {
          TransmittersUiModel.TransmitterList(it)
        }
      }

    // a delayed subscription (others should only subscribe once transmitters have been returned to the controller)
    val delayedSubscriptionUntilCorrectReturnType =
      transformedTransmitters.filter { it is TransmittersUiModel.TransmitterList }

    // get the connectionstate of the transmitters
    val bleConnectionStateObservable = transmittersOrEmptyObservable
      .delaySubscription(delayedSubscriptionUntilCorrectReturnType)
      .filter { it.isNotEmpty() }
      .flatMapIterable { it }
      .map { it.macAddress }
      .flatMap { macAddress ->
        bluetoothManager.bleConnectionStateObservable(macAddress)
          .map { TransmittersUiModel.TransmitterConnectionState(ConnectionState(macAddress, it)) }
      }

    // and return the latest update from the transmitters
    val latestStateObservable = StateDispatcher.stateChangeObservable()
      .delaySubscription(delayedSubscriptionUntilCorrectReturnType)
      .map { TransmittersUiModel.StateUpdate(it) }

    val deleteTransmitterObservable = uiView.requestDeleteTransmitter()
      .switchMap { macAddress ->
        transmitterComponent.deleteTransmitter(macAddress)
          .subscribeOn(Schedulers.io())
          .toSingle { true }
          .toObservable()
          .map {
            ServiceDispatcher.dispatchEvent(ServiceEvent.DisconnectTransmitter(macAddress))
            StateDispatcher.clear(macAddress)
            bluetoothManager.disconnectTransmitter(macAddress)
            TransmittersUiModel.TransmitterDeleted(it)
          }
      }

    val retryConnectionObservable: Observable<TransmittersUiModel> = uiView.retryConnection()
      .switchMap {
        // initiate reconnection retry on the service
        ServiceDispatcher.dispatchEvent(ServiceEvent.RetryConnection())

        // after 5 seconds send back a completed response
        Observable.just(TransmittersUiModel.RetryConnectComplete() as TransmittersUiModel)
          .delay(5, TimeUnit.SECONDS)
          .startWith(TransmittersUiModel.RetryConnectToTransmitters())
      }

    disposable.add(
      Observable.merge(
        transformedTransmitters,
        bleConnectionStateObservable,
        latestStateObservable
      ).bind(uiView.render())
    )

    disposable.add(
      Observable.merge(
        deleteTransmitterObservable,
        retryConnectionObservable
      ).bind(uiView.render())
    )

    return disposable
  }
}