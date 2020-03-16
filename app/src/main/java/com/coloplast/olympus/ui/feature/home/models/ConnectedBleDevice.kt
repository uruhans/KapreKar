package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice

class ConnectedBleDevice(bleDevice: BleDevice) {
  val name = "${bleDevice.name} (${bleDevice.macAddress})"
}