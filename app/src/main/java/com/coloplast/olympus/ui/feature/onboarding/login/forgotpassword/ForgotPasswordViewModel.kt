package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ForgotPasswordViewModel(private val userComponent: UserComponent):
  BaseViewModel<GenericUiModel, ForgotPasswordView>(){
  override fun bind(uiView: ForgotPasswordView): Disposable {
    val disposable = CompositeDisposable()

    return disposable
  }
}