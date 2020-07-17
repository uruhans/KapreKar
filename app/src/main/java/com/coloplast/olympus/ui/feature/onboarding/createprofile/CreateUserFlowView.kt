package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.CreateUserModel
import io.reactivex.Observable

interface CreateUserFlowView: UiView<GenericUiModel> {
  fun onCreateUser(): Observable<CreateUserModel>
}
