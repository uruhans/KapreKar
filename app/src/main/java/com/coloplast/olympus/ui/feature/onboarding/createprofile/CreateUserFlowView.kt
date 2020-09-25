package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.CreateUserFactoryModel
import io.reactivex.Observable

interface CreateUserFlowView: UiView<GenericUiModel> {
  fun onCreateUser(): Observable<User>
  fun sendCode(): Observable<String>
  fun validateCode(): Observable<CreateUserFactoryModel>
}
