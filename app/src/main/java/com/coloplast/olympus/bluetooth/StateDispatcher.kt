package com.coloplast.olympus.bluetooth

import com.coloplast.olympus.bluetooth.model.StateChange
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

object StateDispatcher {
  
  val stateChangeRelay = PublishRelay.create<StateChange>()
  
  fun stateChangeObservable() : Observable<StateChange> {
    return stateChangeRelay.publish().refCount()
  }
}