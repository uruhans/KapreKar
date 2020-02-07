package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class PowerLevel {
  HIGH,
  MEDIUM,
  LOW,
  EMPTY;
  
  companion object {
    fun fromPercentage(percentage: Int): PowerLevel {
      return when {
        percentage >= 75 -> HIGH
        percentage >= 25 -> MEDIUM
        percentage >= 1 -> LOW
        else -> EMPTY
      }
    }
  }
}