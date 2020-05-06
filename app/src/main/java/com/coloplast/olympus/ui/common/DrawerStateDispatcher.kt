package com.coloplast.olympus.ui.common

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

object DrawerStateDispatcher {
  private val changeDrawerStateRelay = PublishRelay.create<DrawerState>()
  
  fun drawerStateObservable(): Observable<DrawerState> {
    return changeDrawerStateRelay
      .publish()
      .refCount()
  }
  
  fun openNavigationDrawerOnHome() {
    changeDrawerStateRelay.accept(DrawerState.OPENED)
  }
  
  fun closeNavigationDrawerOnHome() {
    changeDrawerStateRelay.accept(DrawerState.CLOSED)
  }
}