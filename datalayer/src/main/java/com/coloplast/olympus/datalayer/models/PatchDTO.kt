package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.Json

class PatchDTO(
  @Json(name = "Id")
  val id: String, // combined deviceId + patchId

  @Json(name = "packages")
  val packages: List<TransmitterValue>
)