package com.coloplast.olympus.shared.models

sealed class UpdateResult {
  data class AppUpdateAvailable(val appVersion: AppVersion) : UpdateResult()

  data class FirmwareUpdateAvailable(val firmwareContent: FirmwareContent) : UpdateResult()
  data class NoUpdate(val nothing: Unit = Unit) : UpdateResult()
}