package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class BatteryState(val id: Int) {
  OK(0),
  BATTERY_LOW(1),
  SHUTTING_DOWN(2),
  CHARGING(3),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: UByte): BatteryState {
      return values().associateBy { batteryState: BatteryState -> batteryState.id }[id.toInt()]
        ?: UNKNOWN
    }
  }
}