package com.coloplast.olympus.ui.feature.profile.model

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.models.UiModel

sealed class ProfileUiModel : UiModel {
  data class FetchedUser(val user: User) : ProfileUiModel()
  data class UserUpdated(val success: Boolean = true) : ProfileUiModel()
  data class LoadingData(val loading: Boolean = true) : ProfileUiModel()
  data class UpdateFailed(val failed: Boolean = true) : ProfileUiModel()
}