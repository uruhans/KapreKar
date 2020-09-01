package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import androidx.annotation.StringRes
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerFlowBinding
import com.coloplast.olympus.shared.exceptions.ApiErrorType
import com.coloplast.olympus.ui.common.LoadingController
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ForgotPasswordFlowController : TopMenuFlowFactoryController<ForgotPasswordModel>(),
  ForgotPasswordView,
  BaseDialogController.Listener{

  private val viewModel: ForgotPasswordViewModel by inject()
  private val model: ForgotPasswordFactoryImpl by inject()
  private val securityCodeRelay: PublishRelay<ForgotPasswordModel> = PublishRelay.create()
  private val confirmCodeRelay: PublishRelay<ForgotPasswordModel> = PublishRelay.create()
  private val changePassword: PublishRelay<ForgotPasswordModel> = PublishRelay.create()
  private var dialogController: DialogController? = null

  override fun getFlowFactory(): FlowFactory<ForgotPasswordModel> = model

  override fun onViewBound(viewBinding: ControllerFlowBinding) {
    super.onViewBound(viewBinding)
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun onStepCompleted(currentIndex: Int, data: ForgotPasswordModel): Boolean {
    return when (ForgotPasswordFlow.fromIndex(currentIndex)) {
      ForgotPasswordFlow.ForgotPasswordFP1 -> {
        securityCodeRelay.accept(data)
        true
      }
      ForgotPasswordFlow.ForgotPasswordFP2 -> {
        confirmCodeRelay.accept(data)
        true
      }
      ForgotPasswordFlow.ForgotPasswordFP3 -> {
        changePassword.accept(data)
        true
      }
      else -> super.onStepCompleted(currentIndex, data)
    }
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      val isLoading = uiModel is GenericUiModel.Loading
      LoadingController.toggle(isLoading, router)

      when (uiModel) {
        is GenericUiModel.Success<*> -> when (uiModel.data) {
          ForgotPasswordViewModel.Callback.CODE_REQUESTED,
          ForgotPasswordViewModel.Callback.CODE_VALIDATED,
          ForgotPasswordViewModel.Callback.PASSWORD_CHANGED -> {
            model.incrementAndNext()
          }
        }
        is GenericUiModel.Error -> {
          @StringRes val messageResId = when (uiModel.errorCode) {
            ApiErrorType.AuthInvalidEmail.code -> R.string.onboarding_email_error
            ApiErrorType.AuthInvalidSecurityCode.code -> R.string.onboarding_fp2_wrong_code
            else -> R.string.generic_error_message
          }

          dialogController = DialogController.create(
            targetController = this,
            requestCode = REQUEST_CODE,
            titleId = R.string.generic_error,
            messageId = messageResId,
            positiveButtonId = R.string.generic_ok,
            dismissAllowed = true
          ).apply { show() }
        }
      }
    }
  }

  override fun resendCode() {
    securityCodeRelay.accept(model.getData())
  }

  override fun onCodeRequested(): Observable<ForgotPasswordModel> {
    return securityCodeRelay.publish().refCount()
  }

  override fun onConfirmCode(): Observable<ForgotPasswordModel> {
    return confirmCodeRelay.publish().refCount()
  }

  override fun onChangePassword(): Observable<ForgotPasswordModel> {
    return changePassword.publish().refCount()
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    dialogController?.dismissDialog(false)
    dialogController = null
  }

  companion object {
    private const val CONTROLLER_TAG = "forgot_password_flow_controller"
    private const val REQUEST_CODE = 1001

    fun getRouterTransaction(): RouterTransaction {
      return RouterTransaction.with(ForgotPasswordFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}