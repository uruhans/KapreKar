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

class CreateUserViewModel(
  private val me: Me,
  private val userComponent: UserComponent
) : BaseViewModel<GenericUiModel, CreateUserFlowView>() {

  override fun bind(uiView: CreateUserFlowView): Disposable {
    val disposable = CompositeDisposable()

    val onCreateUserClicked: Observable<GenericUiModel> =
      uiView.onCreateUser()
        .switchMap { user ->
          userComponent.createUser(user)
            .subscribeOn(Schedulers.io())
            .compose(DelayedLoadingTransformer<CurrentUser>())
            .map { delayedResult: DelayedLoadingModel<CurrentUser> ->
              return@map when {
                delayedResult.isLoading -> GenericUiModel.Loading()
                delayedResult.result != null -> {
                  val currentUser = delayedResult.result as CurrentUser
                  me.setCurrentUser(currentUser)

                  if (currentUser is CurrentUser.LoggedIn) {
                    GenericUiModel.Success(currentUser)
                  }
                  else {
                    GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 404)
                  }
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

    disposable.add(onCreateUserClicked.bind(uiView.render()))
    return disposable
  }

}