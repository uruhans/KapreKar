package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.Json

class RawStreamDTO(
  @Json(name = "TransmitterDtos")
  val transmitters : List<TransmitterStreamDTO>
)