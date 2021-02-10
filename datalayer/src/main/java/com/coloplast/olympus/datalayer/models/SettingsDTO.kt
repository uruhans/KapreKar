package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.TransmitterSettings
import java.time.Instant

class SettingsDTO(
  val timeStamp: Long,
  val widRthreshold: Int,
  val widRHysterises: Int,
  val widCThreshold: Int,
  val thresholdCS0: Int,
  val thresholdCS1: Int,
  val thresholdCS2: Int,
  val thresholdCS3: Int,
  val thresholdCS4: Int,
  val thresholdCS5: Int,
  val csHysteresis: Int,
  val batteryThreshold: Int,
  val batteryHysteresis: Int,
  val sample_interval: Int,
  val filter: Int
) {

  companion object {
    fun fromSettings(settings: TransmitterSettings): SettingsDTO {
      return SettingsDTO(
        Instant.now().epochSecond,
        settings.widRthreshold.toInt(),
        settings.widRHysterises.toInt(),
        settings.widCThreshold.toInt(),
        settings.thresholdCS0.toInt(),
        settings.thresholdCS1.toInt(),
        settings.thresholdCS2.toInt(),
        settings.thresholdCS3.toInt(),
        settings.thresholdCS4.toInt(),
        settings.thresholdCS5.toInt(),
        settings.csHysteresis.toInt(),
        settings.batteryThreshold.toInt(),
        settings.batteryHysteresis.toInt(),
        settings.sample_interval.toInt(),
        settings.filter.toInt()
      )
    }
  }
}