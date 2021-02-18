package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class PowerLevel {
  HIGH,
  MEDIUM,
  LOW,
  EMPTY;

  var percentage: Int = 0
  
  companion object {
    fun fromPercentage(percentage: Int): PowerLevel {

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