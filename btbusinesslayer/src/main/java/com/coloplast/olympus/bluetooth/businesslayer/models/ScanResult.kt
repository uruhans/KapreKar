package com.coloplast.olympus.bluetooth.businesslayer.models

data class ScanResult(val name: String?, val macAddress: String, val rssi: Int) {
  fun getNameOrAddress(): String {
    return "$name ($macAddress)"
  }
}