package com.coloplast.olympus.ui.feature.onboarding.signup

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.Observable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
interface SignupFlowView: UiView<GenericUiModel> {
  fun onCreateUser(): Observable<User>
  fun sendCode(): Observable<String>
  fun validateCode(): Observable<SignupFactoryModel>
}