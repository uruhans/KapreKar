package com.coloplast.olympus.bluetooth.businesslayer.characteristics

import com.coloplast.olympus.bluetooth.businesslayer.utils.UUIDHelper
import java.util.*

enum class Services(val uuid: String) {
  BATTERY_POWER_SERVICE("8b02c452-83ab-11e9-9a4b-526af7764f64"),
  OLYMPUS_WIRELESS_PROTOCOL("cc7314cc-003c-40b2-9372-4881e4805672"),
  ;
  
  fun asUUID(): UUID {
    return UUIDHelper.asUUID(uuid)
  }
}