package com.coloplast.olympus.ui.feature.onboarding.login

import androidx.annotation.StringRes
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerLoginBinding
import com.coloplast.olympus.shared.extensions.isValidEmail
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.utils.StringUtils.PASSWORD_REGEX
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.LoadingController
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.extensions.onValueChanged
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 07/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LoginController: BaseController<ControllerLoginBinding>(),
  LoginView,
  BaseDialogController.Listener {

  private val viewModel: LoginViewModel by inject()
  private val loginRelay: PublishRelay<Credentials> = PublishRelay.create()

  private var dialogController: DialogController? = null

  val validEmail = ObservableBoolean(true)
  val email = ObservableField("").apply {
    compositeDisposable.add(
      onValueChanged { validEmail.set(this.get()!!.isValidEmail()) }
    )
  }
  val validPassword = ObservableBoolean(true)
  val password = ObservableField("").apply {
    compositeDisposable.add(
      onValueChanged {
        val regex = PASSWORD_REGEX.toRegex()
        val isValid = get()!!.matches(regex)
        validPassword.set(isValid)
      }
    )
  }

  override fun attachVariables(viewBinding: ControllerLoginBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerLoginBinding) {
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      val isLoading = uiModel is GenericUiModel.Loading
      LoadingController.toggle(isLoading, router)

      when (uiModel) {
        is GenericUiModel.Success<*> -> {
          if(uiModel.data as Boolean == true){
            //Login successful
            //TODO: Go to next screen.
          }
        }
        is GenericUiModel.Error -> {
          @StringRes val messageResId = when (uiModel.errorCode) {
            //22 -> R.string.textfield_error_email_taken // email taken
            //23 -> R.string.textfield_error_invalid_password_missing_uppercase // password too weak
            else -> R.string.generic_error_message
          }

          dialogController = DialogController.create(
            targetController = this,
            requestCode = LOGIN_ERROR_REQUEST_CODE,
            titleId = R.string.generic_error,
            messageId = messageResId,
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
    email.notifyChange()
    password.notifyChange()
    if(validEmail.get() && validPassword.get()){
      val credentials = Credentials(
        email.get()!!,
        password.get()!!

      )
      loginRelay.accept(credentials)
    }

  }

  fun onForgotPassword(){
    //TODO: Link with forgot password flow.
  }

  override fun getLayout(): Int = R.layout.controller_login

  companion object{
    private const val TAG_CONTROLLER = "login_controller"
    private const val LOGIN_ERROR_REQUEST_CODE = 1001

    fun getRouterTransaction() = RouterTransaction.with(LoginController())
      .tag(TAG_CONTROLLER)
  }
}