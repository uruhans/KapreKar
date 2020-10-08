package com.coloplast.olympus.ui.feature.update.progress

import com.coloplast.olympus.ui.common.models.UiView
import com.coloplast.olympus.ui.feature.update.progress.model.FirmwareProgressUiModel
import com.coloplast.olympus.ui.feature.update.progress.model.UpdateModel

interface FirmwareProgressView : UiView<FirmwareProgressUiModel> {
  fun getUpdateModel() : UpdateModel
}