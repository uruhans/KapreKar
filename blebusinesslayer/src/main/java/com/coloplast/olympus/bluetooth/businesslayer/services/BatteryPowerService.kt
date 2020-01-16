package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID
import io.reactivex.ObservableTransformer

object BatteryPowerService {
  val UUID = "8b02c452-83ab-11e9-9a4b-526af7764f64".asUUID()
  
  object Characteristics {
    val POWER = "90ae2388-83ab-11e9-9a4b-526af7764f64".asUUID()
  }
  
  val transformer = ObservableTransformer<ByteArray, PowerLevel> { upstream ->
    upstream.map { PowerLevel.fromPercentage(it[0].toInt()) }
  }
}