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
    
    val storedTransmitters = transmitterComponent.getTransmitters()
      .subscribeOn(Schedulers.io())
      .doOnNext { Timber.d("getTransmitters hit") }
      .map {
        if (it is DataResult.Success<*>) {
          it.data as List<Transmitter>
        }
        else {
          emptyList()
        }
      }
      .doOnNext { Timber.d("size: ${it.size}") }
      .map { TransmittersUiModel.ConnectedDevices(it) as TransmittersUiModel }
      .startWith(TransmittersUiModel.Empty())
    
    val delayedSubscriptionUntilCorrectReturnType = storedTransmitters.filter { it is TransmittersUiModel.ConnectedDevices }
    
    val connectionStateObservable = StateDispatcher.connectionStateObservable()
      .delaySubscription(delayedSubscriptionUntilCorrectReturnType)
      .map { TransmittersUiModel.TransmitterConnectionState(it) }
   
    val latestStateObservable = StateDispatcher.stateChangeObservable()
      .delaySubscription(delayedSubscriptionUntilCorrectReturnType)
      .map { TransmittersUiModel.StateUpdate(it) }
    
    disposable.add(Observable.merge(storedTransmitters, connectionStateObservable, latestStateObservable).bind(uiView.render()))
    return disposable
  }
}