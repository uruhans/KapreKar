package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.Attached
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState

/**
 * a representation of a BleDevice
 *
 * if the peripheral exists it means we are currently connected to the
 */
data class BleDevice(val name: String?, val macAddress: String, val connectionState: BleConnectionState, var peripheral: Peripheral? = null) {
  fun isConnectedOrConnecting() : Boolean {
    return isConnected() || connectionState == BleConnectionState.CONNECTING
  }
  
  fun isConnected() : Boolean {
    return connectionState == BleConnectionState.CONNECTED
  }

  fun isOnPatch() : Boolean {
    return peripheral?.transmitter?.attached == Attached.ATTACHED_ON_BODY || peripheral?.transmitter?.attached == Attached.ATTACHED_ON_PATCH
  }
}