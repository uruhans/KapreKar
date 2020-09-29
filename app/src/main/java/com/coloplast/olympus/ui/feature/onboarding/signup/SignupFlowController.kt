package com.coloplast.olympus.ui.feature.onboarding.signup

import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerFlowBinding
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.LoadingController
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.feature.onboarding.signup.dataconsent.DataConsentFlow
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SignupFlowController : TopMenuFlowFactoryController<SignupFactoryModel>(),
  SignupFlowView,
  BaseDialogController.Listener {

  private val factory by lazy { SignupFactoryImpl() }

  private val preferences: Preferences by inject()
  private val viewModel: SignupViewModel by inject()

  private val registrationRelay: PublishRelay<User> = PublishRelay.create()
  private val sendCodeRelay: PublishRelay<String> = PublishRelay.create()
  private val validateCodeRelay: PublishRelay<SignupFactoryModel> = PublishRelay.create()
  private var dialogController: DialogController? = null

  override fun getFlowFactory(): FlowFactory<SignupFactoryModel> = factory

  override fun startFlow(flowChildRouter: Router) {
    preferences.setCurrentOnboardingProgression(OnboardingProgression.SIGN_UP)
    super.startFlow(flowChildRouter)
  }

  override fun onFlowCompleted(data: SignupFactoryModel) {
    preferences.setCurrentOnboardingProgression(OnboardingProgression.SETUP_GUIDE_START)
    super.onFlowCompleted(data)
  }

  override fun onStepCompleted(currentIndex: Int, data: SignupFactoryModel): Boolean {
    return when (SignupFlow.fromIndex(currentIndex)) {
      SignupFlow.DataConsentDC1 -> {
        return if(data.skip){
          with(factory){
            skipUntil(DataConsentFlow.DataConsentDC5.id)
            incrementAndNext()
          }
          true
        } else {
          false // go to next step as usual.
        }
      }
      SignupFlow.DataConsentDC5 -> {
        if(data.user.consent.not()) {
          activity?.finish()
          return true
        }
        false
      }
      SignupFlow.PhoneSU5 -> {
        sendCodeRelay.accept(data.user.phoneNumber!!)
        false
      }
      SignupFlow.PhoneValidationSU6 -> {
        validateCodeRelay.accept(data)
        true
      }
      SignupFlow.InsuranceSU7 -> {
        registrationRelay.accept(data.user)
        true
      }
      else -> super.onStepCompleted(currentIndex, data)
    }
  }

  override fun onCreateUser(): Observable<User> {
    return registrationRelay
      .publish()
      .refCount()
  }

  override fun sendCode(): Observable<String> {
    return sendCodeRelay
      .publish()
      .refCount()
  }

  override fun validateCode(): Observable<SignupFactoryModel> {
    return validateCodeRelay
      .publish()
      .refCount()
  }

  override fun onViewBound(viewBinding: ControllerFlowBinding) {
    compositeDisposable.add(
      viewModel.bind(this)
    )
    super.onViewBound(viewBinding)
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      val isLoading = uiModel is GenericUiModel.Loading
      LoadingController.toggle(isLoading, router)

      when (uiModel) {
        is GenericUiModel.Success<*> -> {

          when (uiModel.data as? SignupViewModel.Callback) {
            SignupViewModel.Callback.CODE_REQUESTED -> {
              // no required action
            }
            SignupViewModel.Callback.CODE_VALIDATED -> {
              // go to health insurance screen. If not UK number it will automatically create the user.
              factory.incrementAndNext()
            }
            SignupViewModel.Callback.USER_CREATED -> {
              //go to confirmation screen
              with(factory) {
                skipUntil(SignupFlow.ConfirmationSU8.id)
                incrementAndNext()
              }
            }
          }
        }
        is GenericUiModel.Error -> {
          dialogController = DialogController.create(
            targetController = this,
            requestCode = REQUEST_ERROR_REQUEST_CODE,
            titleId = R.string.generic_error,
            messageId = R.string.generic_error_message,
            positiveButtonId = R.string.generic_ok,
            dismissAllowed = true
          ).apply { show() }
        }
      }
    }
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_ERROR_REQUEST_CODE -> {
        dialogController?.dismissDialog(false)
        dialogController = null
      }
    }
  }

  companion object {
    private const val CONTROLLER_TAG = "sign_up_flow_controller"
    private const val REQUEST_ERROR_REQUEST_CODE = 1001

    fun routerTransaction() = RouterTransaction.with(SignupFlowController())
      .tag(CONTROLLER_TAG)
  }
}