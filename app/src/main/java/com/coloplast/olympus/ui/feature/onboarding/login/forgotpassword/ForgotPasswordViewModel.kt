package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import com.coloplast.olympus.ui.common.rx.GenericUiModelTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ForgotPasswordViewModel(private val userComponent: UserComponent):
  BaseViewModel<GenericUiModel, ForgotPasswordView>(){
  override fun bind(uiView: ForgotPasswordView): Disposable {
    val disposable = CompositeDisposable()

    val requestCodeCompletable =
      uiView.onCodeRequested()
        .switchMap { email ->
          userComponent.requestSecurityCode(email)
            .subscribeOn(Schedulers.io())
            .toSingle { true }
            .toObservable()
            .compose(DelayedLoadingTransformer<Boolean>())
            .compose(GenericUiModelTransformer())
        }

    disposable.add(requestCodeCompletable.bind(uiView.render()))
    return disposable
  }
}