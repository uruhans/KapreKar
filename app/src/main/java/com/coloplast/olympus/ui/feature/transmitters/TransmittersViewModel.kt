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
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.transmitters.models.TransmittersUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TransmittersViewModel(
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent,
  private val transmitterManager: TransmitterManager
) : BaseViewModel<TransmittersUiModel, TransmittersView>() {

  override fun bind(uiView: TransmittersView): Disposable {
    val disposable = CompositeDisposable()

    // transmitters stored in database
    val transmittersOrEmptyObservable = transmitterComponent.getTransmitters()
      .subscribeOn(Schedulers.io())
      .map {
        if (it is DataResult.Success<*>) {
          TransmittersUiModel.TransmitterList(it.data as List<Transmitter>)
        }
        else {
          TransmittersUiModel.Empty()
        }
      }

    // get the connectionstate of the transmitters
    val bleConnectionStateObservable = transmittersOrEmptyObservable
      .ofType(TransmittersUiModel.TransmitterList::class.java)
      .flatMapIterable { it.devices }
      .map { it.id }
      .flatMap { macAddress ->
        bluetoothManager.bleConnectionStateObservable(macAddress)
          .doOnNext { Timber.d("Device [$macAddress] is $it") }
          .map { TransmittersUiModel.TransmitterConnectionState(ConnectionState(macAddress, it)) }
      }

    // and return the latest update from the transmitters
    val latestStateObservable = StateDispatcher.stateChangeObservable()
      .delaySubscription(bleConnectionStateObservable)
      .map { TransmittersUiModel.StateUpdate(it) }

    val deleteTransmitterObservable = uiView.requestDeleteTransmitter()
      .switchMap { macAddress ->
        transmitterComponent.deleteTransmitter(macAddress)
          .subscribeOn(Schedulers.io())
          .toSingle { true }
          .toObservable()
          .map {
            transmitterManager.deleteTransmitterHistory(macAddress)
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
        transmittersOrEmptyObservable,
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