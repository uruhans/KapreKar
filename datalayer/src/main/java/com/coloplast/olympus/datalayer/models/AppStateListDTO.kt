package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.Json

class AppStateListDTO(
  @Json(name = "code")
  val appStates: List<AppStateDTO>
)