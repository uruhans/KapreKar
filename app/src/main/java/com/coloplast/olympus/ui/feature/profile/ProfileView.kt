package com.coloplast.olympus.ui.feature.profile

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.models.UiView
import com.coloplast.olympus.ui.feature.profile.model.ProfileUiModel
import io.reactivex.Observable

interface ProfileView : UiView<ProfileUiModel> {
  fun onLogoutRequested(): Observable<Unit>
  fun onUpdateUserRequest(): Observable<User>
}