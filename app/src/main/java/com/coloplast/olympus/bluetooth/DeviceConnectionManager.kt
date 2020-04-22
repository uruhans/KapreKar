package com.coloplast.olympus.bluetooth

import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent
import org.koin.core.get

class DeviceConnectionManager : KoinComponent {
  
  interface Listener {
    fun onResult(deviceResult: DeviceResult)
  }
  
  private val connections = mutableMapOf<String, DeviceConnection>()
  private val disposables = mutableMapOf<String, Disposable>()
  private var listener: Listener? = null
  
  private val resultRelay = PublishRelay.create<DeviceResult>()
  
  private val disposables2 = mutableMapOf<String, Observable<DeviceResult>>()
  
  fun onResultObservable(): Observable<DeviceResult> {
    return resultRelay.publish()
      .refCount()
  }
  
  fun connect(macAddress: String): Observable<DeviceResult> {
    disposables[macAddress]?.dispose()
    disposables.remove(macAddress)
    
    
    //    synchronized(listeners) {
    //      if (!listeners.contains(listener)) {
    //        listeners.add(listener)
    //      }
    //    }
    
    //this.listener = listener
    
    //    if (connections.containsKey(macAddress)) {
    //      return
    //    }
    
    if (!connections.containsKey(macAddress)) {
      get<DeviceConnection>().also { connections[macAddress] = it }
    }
    
    disposables[macAddress] = connections[macAddress]!!.subscribeForNotifications(macAddress)
      .subscribe(
        { deviceResult -> resultRelay.accept(deviceResult) },
        { cleanup(macAddress) }
      )
    
    if (!disposables2.containsKey(macAddress)) {
      disposables2[macAddress] = onResultObservable()
    }
    
    return disposables2[macAddress]!!
  }
  
  fun cleanup() {
    connections.clear()
    
    disposables.forEach { it.value.dispose() }
    disposables.clear()
    listener = null
  }
  
  fun cleanup(macAddress: String) {
    connections.remove(macAddress)
    disposables[macAddress]?.dispose()
    disposables.remove(macAddress)
    this.listener = null
  }
}