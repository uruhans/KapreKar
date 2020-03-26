package com.coloplast.olympus.ui.feature.transmitters.models

import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice

class TransmitterItem(val bleDevice: BleDevice, val listener: Listener) {
  interface Listener {
    fun onDeviceClicked()
  }
}