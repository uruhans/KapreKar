package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class PowerLevel {
  HIGH,
  MEDIUM,
  LOW,
  NEAR_EMPTY;
  
  companion object {
    fun fromPercentage(percentage: Int): PowerLevel {
      return when {
        percentage > 85 -> HIGH
        percentage > 40 -> MEDIUM
        percentage > 5 -> LOW
        else -> NEAR_EMPTY
      }
    }
  }
}