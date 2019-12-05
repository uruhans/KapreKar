package com.coloplast.olympus.bluetooth.businesslayer.enums

import com.polidea.rxandroidble2.RxBleConnection

enum class BleConnectionState {
  CONNECTING,
  CONNECTED,
  DISCONNECTED,
  DISCONNECTING;
  
  companion object {
    fun fromRxBleState(state: RxBleConnection.RxBleConnectionState) : BleConnectionState {
      return when (state) {
        RxBleConnection.RxBleConnectionState.CONNECTING -> CONNECTING
        RxBleConnection.RxBleConnectionState.CONNECTED -> CONNECTED
        RxBleConnection.RxBleConnectionState.DISCONNECTED -> DISCONNECTED
        RxBleConnection.RxBleConnectionState.DISCONNECTING -> DISCONNECTING
      }
    }
  }
}