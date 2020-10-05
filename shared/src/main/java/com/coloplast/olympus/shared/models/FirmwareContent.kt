package com.coloplast.olympus.shared.models

class FirmwareContent(
  val firmwareVersion: String,
  val mandatory: Boolean,
  val filePath: String,
  val contentMD5: String,
  val minimumAndroidVersion: String
) {

  fun isNewer(currentFirmwareVersion: String) : Boolean {
    return currentFirmwareVersion < firmwareVersion
  }

  fun isMinimumAndroidVersion(currentAppVersion: String) : Boolean {
    return currentAppVersion >= minimumAndroidVersion
  }
}