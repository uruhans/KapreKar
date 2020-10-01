package com.coloplast.olympus.shared.models

sealed class FirmwareResult {
  data class UpdateAvailable(val firmwareContent: FirmwareContent) : FirmwareResult() {
    fun isMandatory() : Boolean {
      return firmwareContent.mandatory
    }
  }

  data class NoUpdate(val nothing: Unit = Unit) : FirmwareResult()
}