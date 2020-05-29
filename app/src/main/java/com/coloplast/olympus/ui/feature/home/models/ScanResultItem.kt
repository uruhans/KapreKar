package com.coloplast.olympus.ui.feature.home.models

import androidx.databinding.ObservableBoolean
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult

class ScanResultItem(val scanResult: ScanResult, val listener: Listener) {
  
  interface Listener {
    fun onScanResultClicked(scanResult: ScanResult)
  }

  val selected = ObservableBoolean(false)

  fun onItemClicked() {
    listener.onScanResultClicked(scanResult)
  }

  fun setSelected(selected: Boolean) {
    this.selected.set(selected)
  }
}