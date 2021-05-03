package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.Json
import java.time.Instant

class AppStateDTO(

  @Json(name = "AppState")
  val appState: String,
  @Json(name = "InternalAppState")
  val internalAppState: String,
  @Json(name = "PatchId")
  val patchId: String,
  @Json(name = "TimeStamp")
  val timeStamp: Long
) : TransmitterValue {
  companion object {
    fun fromAppState(appState: String, internalAppState: String, patchId: String, timeStamp: Instant) : AppStateDTO {
      return AppStateDTO(appState, internalAppState, patchId, timeStamp.epochSecond)
    }
  }
}