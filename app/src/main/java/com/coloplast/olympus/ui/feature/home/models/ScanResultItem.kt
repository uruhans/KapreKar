package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult

class ScanResultItem(val scanResult: ScanResult, val listener: Listener) {
  
  interface Listener {
    fun onScanResultClicked(scanResult: ScanResult)
  }
  
  fun onItemClicked() {
    listener.onScanResultClicked(scanResult)
  }
}