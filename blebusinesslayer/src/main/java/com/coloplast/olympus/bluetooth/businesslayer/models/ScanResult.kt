package com.coloplast.olympus.bluetooth.businesslayer.models

/**
 * A class that represents a result during BLE Scanning
 */
data class ScanResult(val name: String?, val macAddress: String, val rssi: Int) {
  
  /**
   * return a string representation of the BleDevice name presented as 'Name (macAddress)'
   */
  fun getNameOrAddress(): String {
    return "$name ($macAddress)"
  }
}