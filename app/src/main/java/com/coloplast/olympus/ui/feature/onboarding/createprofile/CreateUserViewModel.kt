package com.coloplast.olympus.ui.feature.onboarding.createprofile

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
 * Created by Kike Bodi (ebp@dawnhealth.com) on 08/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class CreateUserViewModel(private val userComponent: UserComponent, private val me: Me):
  BaseViewModel<GenericUiModel, CreateUserFlowView>() {

  override fun bind(uiView: CreateUserFlowView): Disposable {
    val disposable = CompositeDisposable()

    val createUserObservable: Observable<GenericUiModel> =
      uiView.onCreateUser()
        .switchMap { createUserModel ->
          userComponent.registration(createUserModel)
            .subscribeOn(Schedulers.io())
            .compose(DelayedLoadingTransformer<CurrentUser>())
            .map { delayedResult: DelayedLoadingModel<CurrentUser> ->
              return@map when {
                delayedResult.isLoading -> GenericUiModel.Loading()
                delayedResult.result != null -> {
                  // registration was successful - set the Me to currentUser
                  me.setCurrentUser(delayedResult.result!!, true)
                  GenericUiModel.Success(delayedResult.result)
                }
                else -> GenericUiModel.Loading()
              }
            }
        }
        .onErrorReturn { throwable: Throwable ->
          GenericUiModel.Error(
            GenericUiModel.Error.State.ERROR_GENERIC, 1000
          )
          // TODO: Align with backend. Do we use CustomErrorModel?
          /*return@onErrorReturn when (throwable) {
            is ServerException -> {
              when (throwable.errorModel?.errorType) {
                ApiErrorType.AuthInvalidEmailOrPassword -> GenericUiModel.Error(
                  GenericUiModel.Error.State.ERROR_NETWORK, ApiErrorType.AuthInvalidEmailOrPassword.code
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
          }*/
        }

    disposable.add(createUserObservable.bind(uiView.render()))
    return disposable
  }

}