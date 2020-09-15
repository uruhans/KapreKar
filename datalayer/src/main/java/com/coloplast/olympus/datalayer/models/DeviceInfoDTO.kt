package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.Json
import java.time.Instant

class DeviceInfoDTO(
  val timeStamp: Instant,
  @Json(name = "manufacture")
  val manufacturer: String?,
  @Json(name = "hwVersion")
  val hardwareRevision: String?,
  @Json(name = "fwVersion")
  val firmwareVersion: String?,
  @Json(name = "serialNo")
  val serialNumber: String?,
  val name: String
)