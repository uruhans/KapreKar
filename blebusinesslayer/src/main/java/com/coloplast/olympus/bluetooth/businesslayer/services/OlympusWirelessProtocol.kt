package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID

object OlympusWirelessProtocol {
  val UUID = "cc7314cc-003c-40b2-9372-4881e4805672".asUUID()
  
  object Characteristics {
    val STATUS = "b9bf73cf-a804-41b8-bf40-a87c93219d91".asUUID()
    val SETTINGS = "3dfc3caf-37fa-47c6-8ca0-b6c0ab4a8202".asUUID()
    val CLASSIFIER_SETTINGS = "3120464a-9be2-11e9-a2a3-2a2ae2dbcce4".asUUID()
    val RAW_SENSOR_DATA = "e2fa0108-b177-4e96-a550-4a47e40adcb7".asUUID()
  }
}