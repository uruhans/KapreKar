package com.coloplast.olympus.bluetooth

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

object StateDbUpdateDispatcher {

  private val dispatchUiStateChangeRelay = PublishRelay.create<String>()

  fun dispatchUiStateChange(uiState: String) {
    dispatchUiStateChangeRelay.accept(uiState)
  }

  fun uiStateChangeObservable() : Observable<String> {
    return dispatchUiStateChangeRelay.publish().refCount()
  }
}