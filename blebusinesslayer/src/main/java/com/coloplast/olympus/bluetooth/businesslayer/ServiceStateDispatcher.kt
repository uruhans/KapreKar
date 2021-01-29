package com.coloplast.olympus.bluetooth.businesslayer

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class ServiceStateDispatcher {

  private val serviceRelay = PublishRelay.create<ServiceState>()

  fun serviceStateObservable(): Observable<ServiceState> {
    return serviceRelay
      .publish()
      .refCount()
  }


  fun startService() {
    serviceRelay.accept(ServiceState.START_SERVICE)
  }

  fun stopService() {
    serviceRelay.accept(ServiceState.STOP_SERVICE)
  }

  enum class ServiceState {
    STOP_SERVICE,
    START_SERVICE
  }
}