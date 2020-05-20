package com.coloplast.olympus.bluetooth

import com.coloplast.olympus.bluetooth.model.ServiceEvent
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

object ServiceDispatcher {

  private val dispatchEventRelay = PublishRelay.create<ServiceEvent>()

  fun dispatchEvent(serviceEvent: ServiceEvent) {
    dispatchEventRelay.accept(serviceEvent)
  }

  fun serviceEventObservable() : Observable<ServiceEvent> {
    return dispatchEventRelay.publish().refCount()
  }
}