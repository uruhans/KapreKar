package com.coloplast.olympus.bluetooth

import com.coloplast.olympus.bluetooth.model.StateChange
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import timber.log.Timber

/**
 * a class that handles the dispatching of transmitter states to any who subscribe
 */
object StateDispatcher {
  
  private val stateChangeRelay = PublishRelay.create<StateChange>()
  private val latestState = mutableMapOf<String, StateChange>()
  
  fun dispatch(stateChange: StateChange) {
    latestState[stateChange.bleDevice.macAddress] = stateChange
    stateChangeRelay.accept(stateChange)
  }
  
  private fun dispatchLatestStateChangesToNewObservers() : Observable<StateChange> {
    return Observable.fromIterable(latestState.values)
  }
  
  /**
   * Subscribe to this observable to get state changes on transmitters
   * NOTICE: it will return states for *all* connected transmitters, so it's your job to determine filter out which you need
   * NOTICE 2: will repeat it's latest state to new subscribers
   */
  fun stateChangeObservable() : Observable<StateChange> {
    return stateChangeRelay.publish()
      .refCount()
      .startWith(dispatchLatestStateChangesToNewObservers())
      .doOnNext { Timber.d("Dispatcher $it") }
  }
}