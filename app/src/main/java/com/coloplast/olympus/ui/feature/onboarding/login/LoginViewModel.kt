package com.coloplast.olympus.ui.feature.onboarding.login

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 14/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LoginViewModel(private val userComponent: UserComponent, private val me: Me) :
  BaseViewModel<GenericUiModel, LoginView>() {

  override fun bind(uiView: LoginView): Disposable {
    val disposable = CompositeDisposable()

    val onLoginRequested: Observable<GenericUiModel> =
      uiView.onLogin()
        .switchMap { credentials ->
          userComponent.login(credentials)
            .subscribeOn(Schedulers.io())
            .compose(DelayedLoadingTransformer<CurrentUser>())
            .map { delayedResult: DelayedLoadingModel<CurrentUser> ->
              return@map when {
                delayedResult.isLoading -> GenericUiModel.Loading()
                delayedResult.result != null -> {
                  me.setCurrentUser(delayedResult.result!!, true)
                  GenericUiModel.Success(true)
                }
                else -> GenericUiModel.Loading()
              }
            }
            //TODO: Backend is not ready. We may have to handle custom errors.
            .onErrorReturn { throwable: Throwable ->
              GenericUiModel.Error(
                GenericUiModel.Error.State.ERROR_GENERIC, 1000
              )
            }
        }

    disposable.add(onLoginRequested.bind(uiView.render()))
    return disposable
  }
}