package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.Observable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
interface ForgotPasswordView: UiView<GenericUiModel> {
  fun onCodeRequested(): Observable<ForgotPasswordModel>
  fun onConfirmCode(): Observable<ForgotPasswordModel>
  fun resendCode()
}