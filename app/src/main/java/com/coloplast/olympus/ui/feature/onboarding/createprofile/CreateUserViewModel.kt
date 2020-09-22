package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.exceptions.ApiErrorType
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.ServerException
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import com.coloplast.olympus.ui.common.rx.GenericUiModelTransformer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

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
    
    val onSendCode: Observable<GenericUiModel> =
      uiView.sendCode()
        .switchMap { phoneNumber ->
          userComponent.requestPhoneValidationSMS(phoneNumber)
            .subscribeOn(Schedulers.io())
            .toSingle { Callback.CODE_REQUESTED }
            .toObservable()
            .compose(DelayedLoadingTransformer<Callback>())
            .compose(GenericUiModelTransformer())
        }

    val confirmCode: Observable<GenericUiModel> =
      uiView.validateCode()
        .switchMap { phoneValidationModel ->
          userComponent.validatePhoneCode(
            phoneValidationModel.phoneNumber,
            phoneValidationModel.code
          )
            .subscribeOn(Schedulers.io())
            .toSingle { Callback.CODE_VALIDATED }
            .toObservable()
            .compose(DelayedLoadingTransformer<Callback>())
            .compose(GenericUiModelTransformer())
        }

    val allObservables = Observable.merge(onCreateUserClicked, onSendCode, confirmCode)
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
    USER_CREATED
  }

}