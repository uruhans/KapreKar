package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState

/**
 * a representation of a BleDevice
 *
 * if the peripheral exists it means we are currently connected to the
 */
data class BleDevice(val name: String?, val macAddress: String, val connectionState: BleConnectionState, val peripheral: Peripheral? = null) {
  fun isConnectedOrConnecting() : Boolean {
    return isConnected() || connectionState == BleConnectionState.CONNECTING
  }
  
  fun isConnected() : Boolean {
    return connectionState == BleConnectionState.CONNECTED
  }
}