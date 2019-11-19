package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class Compromised(val id: Int) {
  NEW(0),
  COMPROMISED(1),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: Int): Compromised {
      return values().associateBy { compromised: Compromised -> compromised.id }[id]
        ?: UNKNOWN
    }
  }
}