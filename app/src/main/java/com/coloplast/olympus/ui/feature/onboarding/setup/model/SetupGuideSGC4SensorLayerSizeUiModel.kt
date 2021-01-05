package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.models.UiModel

sealed class SetupGuideSGC4SensorLayerSizeUiModel : UiModel {
  data class FetchedUser(val user: User) : SetupGuideSGC4SensorLayerSizeUiModel()
  data class UserUpdated(val user: User) : SetupGuideSGC4SensorLayerSizeUiModel()
  data class LoadingData(val loading: Boolean = true) : SetupGuideSGC4SensorLayerSizeUiModel()
  data class UpdateFailed(val failed: Boolean = true) : SetupGuideSGC4SensorLayerSizeUiModel()
}