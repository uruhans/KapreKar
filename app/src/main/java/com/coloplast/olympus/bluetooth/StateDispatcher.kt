package com.coloplast.olympus.bluetooth

import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.models.DataResult
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import timber.log.Timber

/**
 * a class that handles the dispatching of transmitter states to any who subscribe
 */
object StateDispatcher {

  private val activeTxStateChangeRelay = PublishRelay.create<DataResult<StateChange>>()
  private var activeTxLatestState: StateChange? = null

  private val stateChangeRelay = PublishRelay.create<StateChange>()
  private val latestState = mutableMapOf<String, StateChange>()

  private val rawDataPackageRelay = PublishRelay.create<String>()
  private val scanningRelay = PublishRelay.create<Unit>()

  fun dispatch(stateChange: StateChange) {
    latestState[stateChange.bleDevice.macAddress] = stateChange
    stateChangeRelay.accept(stateChange)
  }

  fun dispatch(macAddress: String) {
    rawDataPackageRelay.accept(macAddress)
  }

  fun dispatchScanning() {
    scanningRelay.accept(Unit)
  }

  private fun dispatchLatestStateChangesToNewObservers(): Observable<StateChange> {
    return Observable.fromIterable(latestState.values)
  }

  /**
   * Subscribe to this observable to get state changes on transmitters
   * NOTICE: it will return states for *all* connected transmitters, so it's your job to determine filter out which you need
   * NOTICE 2: will repeat it's latest state to new subscribers
   */
  fun stateChangeObservable(): Observable<StateChange> {
    return stateChangeRelay.publish()
      .refCount()
      .startWith(dispatchLatestStateChangesToNewObservers())
      .doOnNext { Timber.d("Dispatcher $it") }
  }

  fun rawPackageUpdateObservable(): Observable<String> {
    return rawDataPackageRelay.publish()
      .refCount()
  }

  fun scanningObservable(): Observable<Unit> {
    return scanningRelay.publish()
      .refCount()
  }


  fun dispatchStateForActiveTransmitter(stateChange: StateChange) {
    activeTxLatestState = stateChange
    activeTxStateChangeRelay.accept(DataResult.Success(stateChange))
  }

  fun activeTransmitterStateChangeObservable(): Observable<DataResult<StateChange>> {
    val dataResult = if(activeTxLatestState != null) {
      DataResult.Success(activeTxLatestState!!)
    }
    else {
      DataResult.Empty<StateChange>()
    }

    return activeTxStateChangeRelay.publish()
      .refCount()
      .startWith(dataResult)
  }
}