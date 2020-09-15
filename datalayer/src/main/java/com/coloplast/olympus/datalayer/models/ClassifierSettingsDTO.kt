package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.ClassifierSettings
import java.time.Instant

class ClassifierSettingsDTO(
  val timeStamp: Instant,
  val ird_ll: Int,
  val dv_noise_ll: Int,
  val ird_spike_ll: Int,
  val ird_leak_ul: Int,
  val ird_pa_ul: Int,
  val signal_sweat_ll: Int,
  val signal_blinded_ll: Int,
  val c_lsl_0: Int,
  val c_lsl_1: Int,
  val c_lsl_2: Int,
  val c_lsl_3: Int,
  val c_lsl_4: Int,
  val c_lsl_5: Int,
  val state_hold: Int,
  val hist_size: Int,
  val noise_count_ul: Int,
  val no_noise_count_ul: Int
) {

  companion object {
    fun fromClassifierSettings(classifierSettings: ClassifierSettings): ClassifierSettingsDTO {
      return ClassifierSettingsDTO(
        Instant.now(),
        classifierSettings.ird_ll.toInt(),
        classifierSettings.dv_noise_ll.toInt(),
        classifierSettings.ird_spike_ll.toInt(),
        classifierSettings.ird_leak_ul.toInt(),
        classifierSettings.ird_pa_ul.toInt(),
        classifierSettings.signal_sweat_ll.toInt(),
        classifierSettings.signal_blinded_ll.toInt(),
        classifierSettings.c_lsl_0.toInt(),
        classifierSettings.c_lsl_1.toInt(),
        classifierSettings.c_lsl_2.toInt(),
        classifierSettings.c_lsl_3.toInt(),
        classifierSettings.c_lsl_4.toInt(),
        classifierSettings.c_lsl_5.toInt(),
        classifierSettings.state_hold.toInt(),
        classifierSettings.hist_size.toInt(),
        classifierSettings.noise_count_ul.toInt(),
        classifierSettings.no_noise_count_ul.toInt()
      )
    }
  }
}