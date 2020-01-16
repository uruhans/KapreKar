package com.coloplast.olympus.bluetooth.businesslayer.models

import org.threeten.bp.LocalDateTime

data class RawDataPackage(
  val year : UShort,
  val month : UByte,
  val day : UByte,
  val hour : UByte,
  val minute : UByte,
  val seconds : UByte,
  val seconds256 : UByte,
  
  val widR : UShort,
  val s0SensorData : UShort,
  val s1SensorData : UShort,
  val s2SensorData : UShort,
  val s3SensorData : UShort,
  val s4SensorData : UShort,
  val s5SensorData : UShort,
  val cs0Data : UShort,
  val cs1Data : UShort,
  val cs2Data : UShort,
  val cs3Data : UShort,
  val cs4Data : UShort,
  val cs5Data : UShort,
  val widC : UShort,
  
  val battMv : UShort,
  val battLevel : UByte,
  
  val bleDisconnected : UByte,
  val classifierReset: Int,
  
  val accX : Int,
  val accY : Int,
  val accZ : Int,
  
  val activity : UByte,
  val wid : Int,
  val attach : Int,
  val patchId : Int,
  val compromised : Int,
  
  val angleRaw : Int,
  val battPower : Int
) {
  
  fun getAccAngle(): Int {
    return angleRaw
        .times(360)
        .div(255)
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