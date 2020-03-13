package com.coloplast.olympus.bluetooth

import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent
import org.koin.core.get
import timber.log.Timber

class DeviceConnectionManager : KoinComponent {
  
  interface Listener {
    fun onResult(deviceResult: DeviceResult)
  }
  
  private val connections = mutableMapOf<String, DeviceConnection>()
  private val disposables = mutableMapOf<String, Disposable>()
  private val listeners = mutableListOf<Listener>()
  
  fun connect(macAddress: String, listener: Listener) {
    synchronized(listeners) {
      if (!listeners.contains(listener)) {
        listeners.add(listener)
      }
    }
    
    if (connections.containsKey(macAddress)) {
      return
    }
    
    val deviceConnection = get<DeviceConnection>()
    connections[macAddress] = deviceConnection
    
    disposables[macAddress] = deviceConnection.subscribeForNotifications(macAddress)
      .subscribe(
        { deviceResult ->
          // TODO - we need to figure out which device we want results from on any given time
          listeners.forEach { it.onResult(deviceResult) }
        },
        {
          Timber.e("$it")
          //cleanup(macAddress, listener)
          
          Observable.error<Throwable>(it)
        },
        { cleanup(macAddress, listener) }
      )
  }
  
  private fun cleanup(macAddress: String, listener: Listener) {
    connections.remove(macAddress)
    disposables[macAddress]?.dispose()
    disposables.remove(macAddress)
    removeListener(listener)
  }
  
  fun removeListener(listener: Listener) {
    synchronized(listeners) {
      listeners.remove(listener)
    }
  }
}