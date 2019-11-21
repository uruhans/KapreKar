package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class Battery(val id: Int) {
  OK(0),
  BATTERY_LOW(1),
  SHUTTING_DOWN(2),
  CHARGING(3),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: UByte): Battery {
      return values().associateBy { battery: Battery -> battery.id }[id.toInt()]
        ?: UNKNOWN
    }
  }
}