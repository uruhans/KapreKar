package com.coloplast.olympus.ui.feature.onboarding.signup

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.exceptions.ApiErrorType
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.ServerException
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import com.coloplast.olympus.ui.common.rx.GenericUiModelTransformer
import com.coloplast.olympus.utils.Utils
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class SignupViewModel(
  private val me: Me,
  private val userComponent: UserComponent
) : BaseViewModel<GenericUiModel, SignupFlowView>() {

  override fun bind(uiView: SignupFlowView): Disposable {
    val disposable = CompositeDisposable()

    val onCreateUser: Observable<GenericUiModel> =
      uiView.onCreateUser()
        .switchMap { user ->
          userComponent.createUser(user)
            .subscribeOn(Schedulers.io())
            .map {
              me.setCurrentUser(it)
              if(it is CurrentUser.LoggedIn){
                Callback.USER_CREATED
              } else it
            }
            .compose(DelayedLoadingTransformer<Any>())
            .compose(GenericUiModelTransformer())
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
        .switchMap { model ->
          // check if code is Integer and return Error otherwise.
          if(model.phoneValidationCode.toIntOrNull() != null){
            userComponent.validatePhoneCode(
              model.user.phoneNumber!!,
              model.phoneValidationCode.toInt()
            )
              .subscribeOn(Schedulers.io())
              .toSingle { Utils.isUKNumber(model.user.phoneNumber) }
              .toObservable()
              // Check if it's a UK phone number.
              // If not, continue with the flow, otherwise we can create the user already.
              .flatMap { isUkNumber ->
                if (isUkNumber) {
                  userComponent.createUser(model.user)
                    .map {
                      me.setCurrentUser(it)
                      Callback.USER_CREATED
                    }
                } else {
                  Observable.just(Callback.CODE_VALIDATED)
                }
              }
              .compose(DelayedLoadingTransformer<Callback>())
              .compose(GenericUiModelTransformer())
          } else {
            Observable.just(
              GenericUiModel.Error(
                GenericUiModel.Error.State.ERROR_GENERIC,
                -1
              )
            )
          }
        }

    val allObservables = Observable.merge(onCreateUser, onSendCode, confirmCode)
      .onErrorReturn { throwable: Throwable ->
        return@onErrorReturn when (throwable) {
          is ServerException -> {
            when (throwable.errorModel?.errorType) {
              is ApiErrorType -> GenericUiModel.Error(
                GenericUiModel.Error.State.ERROR_NETWORK,
                throwable.errorModel!!.errorType.code
              )
              else -> GenericUiModel.Error(
                GenericUiModel.Error.State.ERROR_NETWORK, 1001
              )
            }
          }
          is IOException -> GenericUiModel.Error(
            GenericUiModel.Error.State.ERROR_NETWORK,
            1001
          )
          else -> GenericUiModel.Error(
            GenericUiModel.Error.State.ERROR_GENERIC, 1000
          )
        }
      }

    disposable.add(allObservables.bind(uiView.render()))
    return disposable
  }

  enum class Callback {
    CODE_REQUESTED,
    CODE_VALIDATED,
    USER_CREATED
  }
}


