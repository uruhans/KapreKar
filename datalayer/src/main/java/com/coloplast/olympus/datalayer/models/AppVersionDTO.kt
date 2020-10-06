package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.AppVersion

class AppVersionDTO(
  val releaseDate: String,
  val version: String,
  val mandatory: Boolean
) {

  fun toAppVersion() : AppVersion {
    return AppVersion(version, mandatory)
  }
}