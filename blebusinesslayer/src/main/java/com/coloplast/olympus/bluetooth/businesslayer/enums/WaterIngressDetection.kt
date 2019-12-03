package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class WaterIngressDetection(val id: Int) {
  OK(0),
  TRIGGERED(1),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: UByte): WaterIngressDetection {
      return values().associateBy { wid: WaterIngressDetection -> wid.id }[id.toInt()]
        ?: UNKNOWN
    }
  }
}