package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class PowerLevel {
  HIGH,
  MEDIUM,
  LOW,
  EMPTY;
  
  companion object {
    fun fromPercentage(percentage: Int): PowerLevel {
      return when {
        percentage >= 76 -> HIGH
        percentage >= 36 -> MEDIUM
        percentage >= 15 -> LOW
        else -> EMPTY
      }
    }
  }
}