package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.StatusPackage
import com.squareup.moshi.Json
import java.time.Instant

class StatusPackageDTO(

  @Json(name = "packageId")
  val id: String, // local storage id (tuple id in sqlite db)

  /**
   * this represents the date that the phone received the raw package from the transmitter
   */
  @Json(name = "timeStamp")
  val receivedDate: Long,

  val classifier0: Int,
  val classifier1: Int,
  val classifier2: Int,
  val classifier3: Int,
  val classifier4: Int,
  val classifier5: Int,
  val wid: Int,
  val attached: Int,
  @Json(name = "batt_pwr")
  val batteryPower: Int,
  val compromised: Int,
  val angle: Int,
  val patchType: Int,
  val fwWarning: Int,
  val fwError: Int
) : TransmitterValue {

  companion object {
    fun fromStatusPackage(id: Int, dataReceived: Instant, statusPackage: StatusPackage) : StatusPackageDTO {
      return StatusPackageDTO(
        id.toString(),
        dataReceived.epochSecond,
        statusPackage.sensor0.toInt(),
        statusPackage.sensor1.toInt(),
        statusPackage.sensor2.toInt(),
        statusPackage.sensor3.toInt(),
        statusPackage.sensor4.toInt(),
        statusPackage.sensor5.toInt(),
        statusPackage.wid.toInt(),
        statusPackage.attached.toInt(),
        statusPackage.batteryState.toInt(),
        statusPackage.compromised.toInt(),
        statusPackage.angle,
        statusPackage.patchType.toInt(),
        statusPackage.fwWarning,
        statusPackage.fwError
      )
    }
  }
}