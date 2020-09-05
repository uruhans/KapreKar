package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.RawDataPackage
import com.squareup.moshi.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class RawPackageDTO(

  @Json(name = "packageId")
  val id: String, // local storage id (tuple id in sqlite db)

  /**
   * this represents the date that the phone received the raw package from the transmitter
   */
  @Json(name = "timeStamp")
  val receivedDate: Instant,

  /**
   * this represents the date specified in the raw package itself
   * ie: based on the first 10 bytes
   *
   *    - year
   *    - month
   *    - day
   *    - hour
   *    - minute
   *    - seconds
   *    - seconds256
   */
  @Json(name = "date")
  val packageDate: Instant,

  @Json(name = "WID_R")
  val widR : Int,

  @Json(name = "V0_sensor")
  val s0SensorData : Int,

  @Json(name = "V1_sensor")
  val s1SensorData : Int,

  @Json(name = "V2_sensor")
  val s2SensorData : Int,

  @Json(name = "V3_sensor")
  val s3SensorData : Int,

  @Json(name = "V4_sensor")
  val s4SensorData : Int,

  @Json(name = "V5_sensor")
  val s5SensorData : Int,

  @Json(name = "CS0")
  val cs0Data : Int,

  @Json(name = "CS1")
  val cs1Data : Int,

  @Json(name = "CS2")
  val cs2Data : Int,

  @Json(name = "CS3")
  val cs3Data : Int,

  @Json(name = "CS4")
  val cs4Data : Int,

  @Json(name = "CS5")
  val cs5Data : Int,

  @Json(name = "WID_C")
  val widC : Int,

  @Json(name = "batt_mv")
  val battMv : Int,

  @Json(name = "batt_level")
  val battLevel : Int,

  @Json(name = "BLE_disconnected")
  val bleDisconnected : Int,

  @Json(name = "classifier_reset")
  val classifierReset: Int,

  @Json(name = "acc_x")
  val accX : Int,

  @Json(name = "acc_y")
  val accY : Int,

  @Json(name = "acc_z")
  val accZ : Int,

  @Json(name = "activity")
  val activity : Int,

  @Json(name = "WID")
  val wid : Int,

  @Json(name = "attach")
  val attach : Int,

  @Json(name = "patch_id")
  val patchId : Int,

  @Json(name = "compromised")
  val compromised : Int,

  @Json(name = "angle")
  val angleRaw : Int,

  @Json(name = "batt_pwr")
  val battPower : Int
)  {

  companion object {
    fun fromRawDataPackage(id: Int, dataReceived: Instant, rawDataPackage: RawDataPackage) : RawPackageDTO {
      return RawPackageDTO(
        id.toString(),
        dataReceived,
        LocalDateTime.of(
          rawDataPackage.year.toInt(),
          rawDataPackage.month.toInt(),
          rawDataPackage.day.toInt(),
          rawDataPackage.hour.toInt(),
          rawDataPackage.minute.toInt(),
          rawDataPackage.seconds.toInt()
        ).toInstant(ZoneOffset.UTC),
        rawDataPackage.widR.toInt(),
        rawDataPackage.s0SensorData.toInt(),
        rawDataPackage.s1SensorData.toInt(),
        rawDataPackage.s2SensorData.toInt(),
        rawDataPackage.s3SensorData.toInt(),
        rawDataPackage.s4SensorData.toInt(),
        rawDataPackage.s5SensorData.toInt(),
        rawDataPackage.cs0Data.toInt(),
        rawDataPackage.cs1Data.toInt(),
        rawDataPackage.cs2Data.toInt(),
        rawDataPackage.cs3Data.toInt(),
        rawDataPackage.cs4Data.toInt(),
        rawDataPackage.cs5Data.toInt(),
        rawDataPackage.widC.toInt(),
        rawDataPackage.battMv.toInt(),
        rawDataPackage.battLevel.toInt(),
        rawDataPackage.bleDisconnected.toInt(),
        rawDataPackage.classifierReset,
        rawDataPackage.accX,
        rawDataPackage.accY,
        rawDataPackage.accZ,
        rawDataPackage.activity.toInt(),
        rawDataPackage.wid,
        rawDataPackage.attach,
        rawDataPackage.patchId,
        rawDataPackage.compromised,
        rawDataPackage.angleRaw,
        rawDataPackage.battPower
      )
    }
  }

}