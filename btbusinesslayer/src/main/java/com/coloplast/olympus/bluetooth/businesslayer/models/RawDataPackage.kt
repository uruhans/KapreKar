package com.coloplast.olympus.bluetooth.businesslayer.models

import org.threeten.bp.LocalDateTime

data class RawDataPackage(
    val wid_r: Int,
    val s0_sensor_data: Int,
    val s1_sensor_data: Int,
    val s2_sensor_data: Int,
    val s3_sensor_data: Int,
    val s4_sensor_data: Int,
    val s5_sensor_data: Int,
    val cs0_data: Int,
    val cs1_data: Int,
    val cs2_data: Int,
    val cs3_data: Int,
    val cs4_data: Int,
    val cs5_data: Int,
    val wid_c: Int,
    val accX: Int,
    val accY: Int,
    val accZ: Int,
    val accAngleRaw: Int,
    val batteryPercentage: Int,
    val batteryVoltageRaw: Int,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val seconds: Int,
    val seconds256: Int
) {
  
  fun getAccAngle(): Int {
    return accAngleRaw.times(360)
        .div(255)
  }
  
  fun getBatteryVoltage(): Int {
    return batteryVoltageRaw.times(8)
        .plus(2600)
  }
  
  fun getDate(): LocalDateTime {
    return LocalDateTime.of(year, month, day, hour, minute, seconds)
  }
}