package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import android.widget.Toast
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.databinding.ControllerFlowBinding
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
class ForgotPasswordFlowController: TopMenuFlowFactoryController<ForgotPasswordModel>(),
  ForgotPasswordView{

  private val viewModel: ForgotPasswordViewModel by inject()
  private val model: FlowFactory<ForgotPasswordModel> by inject()
  private val securityCodeRelay: PublishRelay<String> = PublishRelay.create()

  override fun getFlowFactory(): FlowFactory<ForgotPasswordModel> = model

  override fun onViewBound(viewBinding: ControllerFlowBinding) {
    super.onViewBound(viewBinding)
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->
        Toast.makeText(applicationContext, "Code requested", Toast.LENGTH_LONG).show()
    }
  }

  override fun requestCode(email: String) {
    securityCodeRelay.accept(email)
  }

  override fun onCodeRequested(): Observable<String> {
    return securityCodeRelay.publish().refCount()
  }

  companion object{
    private const val CONTROLLER_TAG = "forgot_password_flow_controller"

    fun getRouterTransaction(): RouterTransaction {
      return RouterTransaction.with(ForgotPasswordFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}