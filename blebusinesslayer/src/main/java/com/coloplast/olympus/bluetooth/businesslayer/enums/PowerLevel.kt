package com.coloplast.olympus.bluetooth.businesslayer.enums

import timber.log.Timber

enum class PowerLevel {
  HIGH,
  MEDIUM,
  LOW,
  EMPTY;

  var percentage: Int = 0

  companion object {
    fun fromPercentage(percentage: Int): PowerLevel {
      Timber.d("Battery Percentage [$percentage]")

      val powerLevel = when {
        percentage >= 76 -> HIGH
        percentage >= 36 -> MEDIUM
        percentage >= 15 -> LOW
        else -> EMPTY
      }
      powerLevel.percentage = percentage
      return powerLevel
    }
  }
}