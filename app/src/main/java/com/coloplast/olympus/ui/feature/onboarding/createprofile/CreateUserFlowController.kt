package com.coloplast.olympus.ui.feature.onboarding.createprofile

import androidx.annotation.StringRes
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerFlowBinding
import com.coloplast.olympus.shared.models.CreateUserModel
import com.coloplast.olympus.ui.common.LoadingController
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.FlowFactoryController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 30/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class CreateUserFlowController: FlowFactoryController<CreateUserModel>(),
  CreateUserFlowView,
  BaseDialogController.Listener {

  private val createUserFactory: FlowFactory<CreateUserModel> by inject()
  private val viewModel: CreateUserViewModel by inject()
  private val registrationRelay: PublishRelay<CreateUserModel> = PublishRelay.create()
  private var dialogController: DialogController? = null

  override fun getFlowFactory(): FlowFactory<CreateUserModel> = createUserFactory

  override fun onStepCompleted(currentIndex: Int, data: CreateUserModel): Boolean {
    return when(CreateUserFlow.fromIndex(currentIndex)){
      CreateUserFlow.PhoneSU6 -> {
        if(data.phoneNumber.contains(UK_COUNTRY_CODE_1) || data.phoneNumber.contains(UK_COUNTRY_CODE_2)){
          registrationRelay.accept(data)
          true
        } else {
          false // go to next step as usual.
        }
      }
      CreateUserFlow.InsuranceSU7 -> {
        registrationRelay.accept(data)
        true
      }
      else -> super.onStepCompleted(currentIndex, data)
    }
  }

  override fun onCreateUser(): Observable<CreateUserModel> {
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
          // User created - Go to last step
          val numberFromUK = uiModel.data as Boolean
          if(numberFromUK) getFlowFactory().skipNext()
          getFlowFactory().incrementAndNext()
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
    when(requestCode){
      REQUEST_ERROR_REQUEST_CODE -> {
        dialogController?.dismissDialog(false)
        dialogController = null
      }
    }
  }

  companion object{
    private const val CONTROLLER_TAG = "create_user_flow_controller"
    private const val UK_COUNTRY_CODE_1 = "+44"
    private const val UK_COUNTRY_CODE_2 = "0044"
    private const val REQUEST_ERROR_REQUEST_CODE = 1001

    fun routerTransaction() = RouterTransaction.with(CreateUserFlowController())
      .tag(CONTROLLER_TAG)
  }
}