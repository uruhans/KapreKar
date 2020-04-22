package com.coloplast.olympus.ui.feature.transmitters

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
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
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TransmittersViewModel(private val bluetoothManager: BluetoothManager, private val transmitterComponent: TransmitterComponent) :
    BaseViewModel<TransmittersUiModel, TransmittersView>() {
  
  override fun bind(uiView: TransmittersView): Disposable {
    val disposable = CompositeDisposable()
    
    val connectedDevices = transmitterComponent.getTransmitters()
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
    
    val transmitterStateObservable = StateDispatcher.stateChangeObservable()
    
    val transmitters = connectedDevices
      .zipWith(
        transmitterStateObservable,
        BiFunction { transmitters: List<Transmitter>, states: StateChange ->
          TransmittersUiModel.ConnectedDevices(transmitters, states)
        })
    
    
    disposable.add(
      Observable.merge(transmitterStateObservable.map { TransmittersUiModel.StateUpdate(it) }.delay(20, TimeUnit.SECONDS), transmitters)
        .bind(uiView.render())
    )
    return disposable
  }
}