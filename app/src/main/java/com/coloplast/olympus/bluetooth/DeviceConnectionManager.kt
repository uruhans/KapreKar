package com.coloplast.olympus.bluetooth

import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent
import org.koin.core.get
import timber.log.Timber

class DeviceConnectionManager() : KoinComponent {
  
  interface Listener {
    fun onResult(deviceResult: DeviceResult)
  }
  
  private val connections = mutableMapOf<String, DeviceConnection>()
  private val disposables = mutableMapOf<String, Disposable>()
  private val listeners = mutableListOf<Listener>()
  
  fun connect(macAddress: String, listener: Listener) {
    if (!listeners.contains(listener)) {
      Timber.d("adding listener $listener")
      listeners.add(listener)
    }
    
    if (connections.containsKey(macAddress)) {
      return
    }
    
    val deviceConnection = get<DeviceConnection>()
    connections[macAddress] = deviceConnection
    
    disposables[macAddress] = deviceConnection.subscribeForNotifications(macAddress)
      .subscribe(
        { deviceResult -> listeners.forEach { it.onResult(deviceResult) } },
        { throwable ->
          Timber.e("$throwable")
          cleanup(macAddress, listener)
        },
        { cleanup(macAddress, listener) }
      )
  }
  
  private fun cleanup(macAddress: String, listener: Listener) {
    connections.remove(macAddress)
    disposables[macAddress]?.dispose()
    disposables.remove(macAddress)
    listeners.remove(listener)
  }
}