package com.coloplast.olympus.ui.feature.onboarding.login

import androidx.databinding.ObservableBoolean
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerLoginBinding
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.LoadingController
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.ForgotPasswordFlowController
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 07/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LoginController: CustomToolbarController<ControllerLoginBinding>(),
  LoginView,
  BaseDialogController.Listener {

  private val viewModel: LoginViewModel by inject()
  private val preferences: Preferences by inject()

  private val loginRelay: PublishRelay<Credentials> = PublishRelay.create()

  private var dialogController: DialogController? = null

  private var email: String = ""
  private var validEmail: Boolean = false
  private var password: String = ""
  private var validPassword: Boolean = false

  val onCurrentEmailChanged: ((String) -> Unit) = {
    email = it
  }

  val onEmailValidityChanged: ((Boolean) -> Unit) = {
    validEmail = it
  }

  val onCurrentPasswordChanged: ((String) -> Unit) = {
    password = it
  }

  val onPasswordValidityChanged: ((Boolean) -> Unit) = {
    validPassword = it
  }

  val fieldsValidationTrigger = ObservableBoolean(false)

  override fun attachVariables(viewBinding: ControllerLoginBinding) {
    viewBinding.controller = this
    super.attachVariables(viewBinding)
  }

  override fun onViewBound(viewBinding: ControllerLoginBinding) {
    compositeDisposable.add(viewModel.bind(this))
    super.onViewBound(viewBinding)
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      val isLoading = uiModel is GenericUiModel.Loading
      LoadingController.toggle(isLoading, router)

      when (uiModel) {
        is GenericUiModel.Success<*> -> {
          if(uiModel.data == true){
            preferences.setCurrentOnboardingProgression(OnboardingProgression.COMPLETED)
            preferences.onboardingCompletedTime = System.currentTimeMillis()

            //Login successful
            getContentRouter().setRoot(HomeController.routerTransaction()
              .popChangeHandler(VerticalChangeHandler())
              .pushChangeHandler(VerticalChangeHandler()))
          } else {
            dialogController = DialogController.create(
              targetController = this,
              requestCode = LOGIN_ERROR_REQUEST_CODE,
              titleId = R.string.generic_error,
              messageId = R.string.onboarding_login_error,
              positiveButtonId = R.string.generic_ok,
              dismissAllowed = true
            ).apply { show() }
          }
        }
        is GenericUiModel.Error -> {
          dialogController = DialogController.create(
            targetController = this,
            requestCode = LOGIN_ERROR_REQUEST_CODE,
            titleId = R.string.generic_error,
            messageId = R.string.generic_error_message,
            positiveButtonId = R.string.generic_ok,
            dismissAllowed = true
          ).apply { show() }
        }
      }
    }
  }

  override fun onLogin(): Observable<Credentials> {
    return loginRelay
      .publish()
      .refCount()
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    when(requestCode){
      LOGIN_ERROR_REQUEST_CODE -> {
        dialogController?.dismissDialog(false)
        dialogController = null
      }
    }
  }

  fun onNextClicked(){
    with(fieldsValidationTrigger){
      set(true)
      notifyChange()
    }
    if(validEmail && validPassword){
      val credentials = Credentials(email, password)
      loginRelay.accept(credentials)
    }
  }

  fun onForgotPassword(){
    getContentRouter().pushController(ForgotPasswordFlowController.getRouterTransaction()
      .popChangeHandler(HorizontalChangeHandler())
      .pushChangeHandler(HorizontalChangeHandler())
    )
  }

  override fun displayCloseButton(): Boolean = false

  override fun getLayout(): Int = R.layout.controller_login

  companion object{
    private const val TAG_CONTROLLER = "login_controller"
    private const val LOGIN_ERROR_REQUEST_CODE = 1001

    fun getRouterTransaction() = RouterTransaction.with(LoginController())
      .tag(TAG_CONTROLLER)
  }
}