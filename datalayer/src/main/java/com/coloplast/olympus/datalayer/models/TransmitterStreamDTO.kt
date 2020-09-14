package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.Json

class TransmitterStreamDTO(
  @Json(name = "Id")
  val transmitterId: String,

  @Json(name = "SensorPatchDtos")
  val patches: List<PatchDTO>
)