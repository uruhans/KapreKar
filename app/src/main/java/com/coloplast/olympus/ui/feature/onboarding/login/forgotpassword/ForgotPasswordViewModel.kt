package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.exceptions.ApiErrorType
import com.coloplast.olympus.shared.models.ServerException
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import com.coloplast.olympus.ui.common.rx.GenericUiModelTransformer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ForgotPasswordViewModel(private val userComponent: UserComponent):
  BaseViewModel<GenericUiModel, ForgotPasswordView>(){
  override fun bind(uiView: ForgotPasswordView): Disposable {
    val disposable = CompositeDisposable()

    val requestCodeObservable =
      uiView.onCodeRequested()
        .switchMap { forgotPasswordModel ->
          userComponent.requestSecurityCode(forgotPasswordModel.email)
            .subscribeOn(Schedulers.io())
            .toSingle { Callback.CODE_REQUESTED }
            .toObservable()
            .compose(DelayedLoadingTransformer<Callback>())
            .compose(GenericUiModelTransformer())
        }

    val confirmCodeObservable =
      uiView.onConfirmCode()
        .switchMap { forgotPasswordModel: ForgotPasswordModel ->
          userComponent.validateSecurityCode(forgotPasswordModel.code, forgotPasswordModel.email)
            .subscribeOn(Schedulers.io())
            .toSingle { Callback.CODE_VALIDATED }
            .toObservable()
            .compose(DelayedLoadingTransformer<Callback>())
            .compose(GenericUiModelTransformer())
        }

    val changePasswordObservable =
      uiView.onChangePassword()
        .switchMap { forgotPasswordModel: ForgotPasswordModel ->
          userComponent.changePassword(
            forgotPasswordModel.code,
            forgotPasswordModel.email,
            forgotPasswordModel.newPassword
          )
            .subscribeOn(Schedulers.io())
            .toSingle { Callback.PASSWORD_CHANGED }
            .toObservable()
            .compose(DelayedLoadingTransformer<Callback>())
            .compose(GenericUiModelTransformer())
        }

    val allObservables = Observable.merge(requestCodeObservable, confirmCodeObservable, changePasswordObservable)
      .onErrorReturn { throwable: Throwable ->
        return@onErrorReturn when (throwable) {
          is ServerException -> {
            when (throwable.errorModel?.errorType) {
              is ApiErrorType -> GenericUiModel.Error(
                GenericUiModel.Error.State.ERROR_NETWORK, throwable.errorModel!!.errorType.code
              )
              else -> GenericUiModel.Error(
                GenericUiModel.Error.State.ERROR_NETWORK, 1001
              )
            }
          }
          is IOException -> GenericUiModel.Error(GenericUiModel.Error.State.ERROR_NETWORK, 1001)
          else -> GenericUiModel.Error(
            GenericUiModel.Error.State.ERROR_GENERIC, 1000
          )
        }
      }

    disposable.add(allObservables.bind(uiView.render()))
    return disposable
  }

  enum class Callback{
    CODE_REQUESTED,
    CODE_VALIDATED,
    PASSWORD_CHANGED
  }
}