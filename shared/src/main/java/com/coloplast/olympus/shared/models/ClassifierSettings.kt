package com.coloplast.olympus.shared.models

class ClassifierSettings(
  val ird_ll: UShort,
  val dv_noise_ll: Short,
  val ird_spike_ll: Short,
  val ird_leak_ul: UShort,
  val ird_pa_ul: UShort,
  val signal_sweat_ll: UShort,
  val signal_blinded_ll: UShort,
  val c_lsl_0: UShort,
  val c_lsl_1: UShort,
  val c_lsl_2: UShort,
  val c_lsl_3: UShort,
  val c_lsl_4: UShort,
  val c_lsl_5: UShort,
  val state_hold: UByte,
  val hist_size: UByte,
  val noise_count_ul: UByte,
  val no_noise_count_ul: UByte
)