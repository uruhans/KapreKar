package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.Json

class PatchDTO(
  val id: String, // combined deviceId + patchId

  @Json(name = "rawPackages")
  val packages: List<TransmitterPackage>
)