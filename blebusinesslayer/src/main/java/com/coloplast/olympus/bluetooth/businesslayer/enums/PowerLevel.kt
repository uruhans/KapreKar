package com.coloplast.olympus.bluetooth.businesslayer.enums

import timber.log.Timber

enum class PowerLevel {
  HIGH,
  MEDIUM,
  LOW,
  EMPTY;
  
  companion object {
    fun fromPercentage(percentage: Int): PowerLevel {
      Timber.e("Battery Percentage [$percentage]")

      return when {
        percentage >= 76 -> HIGH
        percentage >= 36 -> MEDIUM
        percentage >= 15 -> LOW
        else -> EMPTY
      }
    }
  }
}