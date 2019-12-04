package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID

object DeviceInformation {
  val UUID = "180a".asUUID()
  
  object Characteristics {
    val MANUFACTURER_NAME = "2a29".asUUID()
    val HARDWARE_REVISION = "2a27".asUUID()
    val FIRMWARE_VERSION = "2a26".asUUID()
    val SERIAL_NUMBER = "2a25".asUUID()
  }
}