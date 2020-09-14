package com.coloplast.olympus.shared.models

class TransmitterSettings(
  val widRthreshold: UShort,
  val widRHysterises: UShort,
  val widCThreshold: UShort,
  val thresholdCS0: UShort,
  val thresholdCS1: UShort,
  val thresholdCS2: UShort,
  val thresholdCS3: UShort,
  val thresholdCS4: UShort,
  val thresholdCS5: UShort,
  val csHysteresis: UShort,
  val batteryThreshold: UByte,
  val batteryHysteresis: UByte,
  val sample_interval: UByte,
  val filter: UByte
)