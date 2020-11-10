package com.coloplast.olympus.bluetooth.businesslayer

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class ServiceStateDispatcher {

  val serviceRelay = PublishRelay.create<ServiceState>()

  fun serviceStateObservable(): Observable<ServiceState> {
    return serviceRelay
      .publish()
      .refCount()
  }

  enum class ServiceState {
    STOP_SERVICE,
    START_SERVICE
  }
}