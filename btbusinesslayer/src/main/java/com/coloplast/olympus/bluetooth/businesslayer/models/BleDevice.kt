package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState

data class BleDevice(val name: String?, val macAddress: String, val connectionState: BleConnectionState, val peripheral: Peripheral? = null) {
  fun isConnected() : Boolean {
    return isFullyConnected() || connectionState == BleConnectionState.CONNECTING
  }
  
  fun isFullyConnected() : Boolean {
    return connectionState == BleConnectionState.CONNECTED
  }
}