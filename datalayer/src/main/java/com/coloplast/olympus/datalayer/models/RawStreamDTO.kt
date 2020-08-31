package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.Json

class RawStreamDTO(
  @Json(name = "id")
  val transmitterId: String,

  @Json(name = "sensorPatchDtos")
  val patches: List<PatchDTO>
)