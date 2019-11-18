package com.coloplast.olympus.bluetooth.businesslayer.characteristics

import com.coloplast.olympus.bluetooth.businesslayer.utils.UUIDHelper
import java.util.*

enum class Characteristics(private val uuid: String) {
  POWER("90ae2388-83ab-11e9-9a4b-526af7764f64"),
  STATUS("b9bf73cf-a804-41b8-bf40-a87c93219d91"),
  ;
  
  fun asUUID(): UUID {
    return UUIDHelper.asUUID(uuid)
  }
}