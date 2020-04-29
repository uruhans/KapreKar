package com.coloplast.olympus.ui.feature.transmitters

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.model.ConnectionState
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.transmitters.models.TransmittersUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TransmittersViewModel(private val bluetoothManager: BluetoothManager, private val transmitterComponent: TransmitterComponent) :
    BaseViewModel<TransmittersUiModel, TransmittersView>() {
  
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
      .cache()
      
    // transforming the stored transmitters into UiModel to be displayed on the screen
    val transformedTransmitters = transmittersOrEmptyObservable
      .map { TransmittersUiModel.ConnectedDevices(it) as TransmittersUiModel }
      .startWith(TransmittersUiModel.Empty())
    
    // a delayed subscription (others should only subscribe once transmitters have been returned to the controller)
    val delayedSubscriptionUntilCorrectReturnType = transformedTransmitters.filter { it is TransmittersUiModel.ConnectedDevices }
  
    // this is to prevent multiple subscriptions on the same transmitter
    val connectedTransmitters = mutableListOf<String>()
    
    // get the connectionstate of the transmitters
    val bleConnectionStateObservable = transmittersOrEmptyObservable
      .delaySubscription(delayedSubscriptionUntilCorrectReturnType)
      .filter { it.isNotEmpty() }
      .flatMapIterable { it }
      .map { it.macAddress }
      .filter { macAddress -> connectedTransmitters.none { it == macAddress } }
      .flatMap { macAddress ->
        bluetoothManager.bleConnectionStateObservable(macAddress)
          .doOnNext {
            if(it == BleConnectionState.CONNECTED) {
              connectedTransmitters.add(macAddress)
            }
            else {
              connectedTransmitters.remove(macAddress)
            }
          }
          .doOnNext { Timber.d("transmitter: $macAddress - $it") }
          .map { TransmittersUiModel.TransmitterConnectionState(ConnectionState(macAddress, it)) as TransmittersUiModel }
      }
   
    // and return the latest update from the transmitters
    val latestStateObservable = StateDispatcher.stateChangeObservable()
      .delaySubscription(delayedSubscriptionUntilCorrectReturnType)
      .map { TransmittersUiModel.StateUpdate(it) }
    
    val deleteTransmitterObservable = uiView.requestDeleteTransmitter()
      .switchMap {macAddress ->
        transmitterComponent.deleteTransmitter(macAddress)
          .subscribeOn(Schedulers.io())
          .toSingle { true }
          .toObservable()
          .map { TransmittersUiModel.TransmitterDeleted(it) }
      }
    
    disposable.add(Observable.merge(transformedTransmitters, bleConnectionStateObservable, latestStateObservable, deleteTransmitterObservable).bind(uiView.render()))
    return disposable
  }
}