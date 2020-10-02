package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID

object OtaFirmware {
  val UUID = "00060000-f8ce-11e4-abf4-0002a5d5c51b".asUUID()

  object Characteristics {
    val OTA_UPDATE = "00060001-f8ce-11e4-abf4-0002a5d5c51b".asUUID()
  }
}