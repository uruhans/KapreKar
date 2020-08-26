package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class Attached(val id: Int) {
  DETACHED(0),
  ATTACHED_ON_PATCH(1),
  ATTACHED_ON_BODY(2),
  ATTACHED_BLE_OFF(129),
  ATTACHED_BODY_BLE(130),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: UByte): Attached {
      return values().associateBy { attached: Attached -> attached.id }[id.toInt()]
        ?: UNKNOWN
    }
  }
  
}