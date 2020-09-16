package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.Json
import java.time.Instant

class PowerPackageDTO(

  @Json(name = "packageId")
  val id: String, // local storage id (tuple id in sqlite db)

  /**
   * this represents the date that the phone received the raw package from the transmitter
   */
  @Json(name = "timeStamp")
  val receivedDate: Instant,
  val powerLevel: Int
) : TransmitterValue {
  companion object {
    fun fromPowerPackage(id: Int, dataReceived: Instant, powerLevel: Int) : PowerPackageDTO {
      return PowerPackageDTO(id.toString(), dataReceived, powerLevel)
    }
  }
}