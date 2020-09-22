package com.coloplast.olympus.ui.feature.onboarding.createprofile

import androidx.annotation.StringRes
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerFlowBinding
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.LoadingController
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.FlowFactoryController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.CreateUserFactoryModel
import com.coloplast.olympus.utils.Utils
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 30/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class CreateUserFlowController : FlowFactoryController<CreateUserFactoryModel>(),
  CreateUserFlowView,
  BaseDialogController.Listener {

  private val createUserFactory: CreateUserFactoryImpl by inject()
  private val preferences: Preferences by inject()

  private val viewModel: CreateUserViewModel by inject()
  private val registrationRelay: PublishRelay<User> = PublishRelay.create()
  private var dialogController: DialogController? = null

  override fun startFlow(flowChildRouter: Router) {
    preferences.setCurrentOnboardingProgression(OnboardingProgression.SIGN_UP)
    super.startFlow(flowChildRouter)
  }

  override fun getFlowFactory(): FlowFactory<CreateUserFactoryModel> = createUserFactory

  override fun onFlowCompleted(data: CreateUserFactoryModel) {
    preferences.setCurrentOnboardingProgression(OnboardingProgression.TERMS_AND_CONSENT)
    super.onFlowCompleted(data)
  }

  override fun onStepCompleted(currentIndex: Int, data: CreateUserFactoryModel): Boolean {
    return when (CreateUserFlow.fromIndex(currentIndex)) {
      CreateUserFlow.PhoneSU5 -> {
        if (Utils.isUKNumber(data.user.phoneNumber)) {
          registrationRelay.accept(data.user)
          true
        }
        else {
          false // go to next step as usual.
        }
      }
      CreateUserFlow.InsuranceSU7 -> {
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
          val currentUser = uiModel.data as CurrentUser.LoggedIn

          with(createUserFactory){
            if (Utils.isUKNumber(currentUser.user.phoneNumber)) {
              skipUntil(CreateUserFlow.ConfirmationSU8.id)
            }
            incrementAndNext()
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
            requestCode = REQUEST_ERROR_REQUEST_CODE,
            titleId = R.string.generic_error,
            messageId = messageResId,
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
    private const val CONTROLLER_TAG = "create_user_flow_controller"
    private const val REQUEST_ERROR_REQUEST_CODE = 1001

    fun routerTransaction() = RouterTransaction.with(CreateUserFlowController())
      .tag(CONTROLLER_TAG)
  }
}