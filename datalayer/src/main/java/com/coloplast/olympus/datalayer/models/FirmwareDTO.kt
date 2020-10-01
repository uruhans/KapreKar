package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.FirmwareContent

class FirmwareDTO(
  val releaseDate: String,
  val firmwareVersion: String,
  val mandatory: Boolean,
  val filePath: String,
  val contentMD5: String,
  val minimumIosVersion: String,
  val minimumAndroidVersion: String
) {

  fun toFirmware() : FirmwareContent {
    return FirmwareContent(
      firmwareVersion,
      mandatory,
      filePath,
      contentMD5,
      minimumAndroidVersion
    )
  }
}

