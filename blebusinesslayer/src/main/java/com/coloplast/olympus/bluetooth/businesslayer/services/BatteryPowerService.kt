package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID

object BatteryPowerService {
  val UUID = "8b02c452-83ab-11e9-9a4b-526af7764f64".asUUID()
  
  object Characteristics {
    val POWER = "90ae2388-83ab-11e9-9a4b-526af7764f64".asUUID()
  }
}