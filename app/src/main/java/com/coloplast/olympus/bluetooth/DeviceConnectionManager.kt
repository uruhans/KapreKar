package com.coloplast.olympus.bluetooth

import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent
import org.koin.core.get

class DeviceConnectionManager : KoinComponent {
  
  private val connections = mutableMapOf<String, DeviceConnection>()
  private val disposables = mutableMapOf<String, Disposable>()
  
  private val resultRelay = PublishRelay.create<DeviceResult>()
  private val observables = mutableMapOf<String, Observable<DeviceResult>>()
  
  private fun onResultObservable(): Observable<DeviceResult> {
    return resultRelay.publish()
      .refCount()
  }
  
  fun connect(macAddress: String): Observable<DeviceResult> {
    return get<DeviceConnection>().subscribeForNotifications(macAddress)
    
//    disposables[macAddress]?.dispose()
//    disposables.remove(macAddress)
//
//    if (!connections.containsKey(macAddress)) {
//      get<DeviceConnection>().also { connections[macAddress] = it }
//    }
//
//    disposables[macAddress] = connections[macAddress]!!.subscribeForNotifications(macAddress)
//      .subscribe(
//        { deviceResult -> resultRelay.accept(deviceResult) },
//        { cleanup(macAddress) }
//      )
//
//    if (!observables.containsKey(macAddress)) {
//      observables[macAddress] = onResultObservable()
//    }
//
//    return observables[macAddress]!!
  }
  
  fun cleanup() {
    connections.clear()
    disposables.forEach { it.value.dispose() }
    disposables.clear()
  }
  
  fun cleanup(macAddress: String) {
    connections.remove(macAddress)
    disposables[macAddress]?.dispose()
    disposables.remove(macAddress)
  }
}