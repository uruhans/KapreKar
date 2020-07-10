package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.databinding.ControllerFlowBinding
import com.coloplast.olympus.shared.models.CreateUserModel
import com.coloplast.olympus.ui.common.LoadingController
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
class CreateUserFlowController: FlowFactoryController<CreateUserModel>(), CreateUserFlowView {

  private val createUserFactory: FlowFactory<CreateUserModel> by inject()
  private val viewModel: CreateUserViewModel by inject()
  private val registrationRelay: PublishRelay<CreateUserModel> = PublishRelay.create()

  override fun getFlowFactory(): FlowFactory<CreateUserModel> = createUserFactory

  override fun onStepCompleted(currentIndex: Int, data: CreateUserModel): Boolean {
    return when(CreateUserFlow.fromId(currentIndex)){
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
        is GenericUiModel.Success<*> -> getFlowFactory().next() // User created - Go to last step
        is GenericUiModel.Error -> {
          /*@StringRes val messageResId = when (uiModel.errorCode) {
            22 -> R.string.textfield_error_email_taken // email taken
            23 -> R.string.textfield_error_invalid_password_missing_uppercase // password too weak
            else -> R.string.alert_generic_request_failed_text
          }

          dialogController = DialogController.create(
            this,
            uiModel.errorCode,
            R.string.textfield_error_alert_title,
            messageResId,
            R.string.button_ok,
            dismissAllowed = true
          ).apply { show() }*/
        }
      }
    }
  }

  companion object{
    private const val CONTROLLER_TAG = "create_user_flow_controller"

    fun routerTransaction() = RouterTransaction.with(CreateUserFlowController())
      .tag(CONTROLLER_TAG)
  }
}