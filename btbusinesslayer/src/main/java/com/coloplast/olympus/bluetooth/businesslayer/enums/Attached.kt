package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class Attached(val id: Int) {
  DETACHED(0),
  ATTACHED_ON_PATCH(1),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: UByte): Attached {
      return values().associateBy { attached: Attached -> attached.id }[id.toInt()]
        ?: UNKNOWN
    }
  }
  
}