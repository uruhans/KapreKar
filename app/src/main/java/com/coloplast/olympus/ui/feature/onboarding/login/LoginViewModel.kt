package com.coloplast.olympus.ui.feature.onboarding.login

import com.coloplast.olympus.Me
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 14/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LoginViewModel(private val userComponent: UserComponent, private val me: Me):
  BaseViewModel<GenericUiModel, LoginView>() {

  override fun bind(uiView: LoginView): Disposable {
    val disposable = CompositeDisposable()

    return disposable
  }

}