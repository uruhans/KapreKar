package com.coloplast.olympus.bluetooth.businesslayer.models

class AdditionalDeviceInfo(
  val deviceInfo: DeviceInfo,
  val settings: ByteArray,
  val classifierSettings: ByteArray
)