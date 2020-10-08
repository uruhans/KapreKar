package com.coloplast.olympus.ui.feature.update.progress.model

import com.coloplast.olympus.ui.common.models.UiModel

sealed class FirmwareProgressUiModel : UiModel {

  data class ProgressUpdate(val currentProgress: Int) : FirmwareProgressUiModel()
  data class UpdateCompleted(val completed: Boolean = true) : FirmwareProgressUiModel()
  data class InvalidFirmwareSignature(val invalid: Boolean = true) : FirmwareProgressUiModel()

  data class Error(val state: State = State.ERROR_GENERIC) : FirmwareProgressUiModel() {
    enum class State {
      ERROR_GENERIC
    }
  }
}