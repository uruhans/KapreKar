package com.coloplast.olympus.bluetooth.businesslayer.models

import org.threeten.bp.LocalDateTime

data class RawDataPackage(
    val wid_r: UShort,
    val s0_sensor_data: UShort,
    val s1_sensor_data: UShort,
    val s2_sensor_data: UShort,
    val s3_sensor_data: UShort,
    val s4_sensor_data: UShort,
    val s5_sensor_data: UShort,
    val cs0_data: UShort,
    val cs1_data: UShort,
    val cs2_data: UShort,
    val cs3_data: UShort,
    val cs4_data: UShort,
    val cs5_data: UShort,
    val wid_c: UShort,
    val accX: Byte,
    val accY: Byte,
    val accZ: Byte,
    val accAngleRaw: UByte,
    val batteryPercentage: UByte,
    val batteryVoltageRaw: UByte,
    val year: UShort,
    val month: UByte,
    val day: UByte,
    val hour: UByte,
    val minute: UByte,
    val seconds: UByte,
    val seconds256: UByte
) {
  
  fun getAccAngle(): Int {
    return accAngleRaw
        .times(360u)
        .div(255u)
        .toInt()
  }
  
  fun getBatteryVoltage(): Int {
    return batteryVoltageRaw
        .times(8u)
        .plus(2600u)
        .toInt()
  }
  
  fun getLocalDateTime(): LocalDateTime {
    return LocalDateTime.of(
      year.toInt(),
      month.toInt(),
      day.toInt(),
      hour.toInt(),
      minute.toInt(),
      seconds.toInt()
    )
  }
}