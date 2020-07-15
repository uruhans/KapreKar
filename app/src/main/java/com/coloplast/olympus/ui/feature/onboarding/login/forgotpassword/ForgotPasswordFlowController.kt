package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ForgotPasswordFlowController: TopMenuFlowFactoryController<ForgotPasswordModel>() {

  private val model: FlowFactory<ForgotPasswordModel> by inject()

  override fun getFlowFactory(): FlowFactory<ForgotPasswordModel> = model

  companion object{
    private const val CONTROLLER_TAG = "forgot_password_flow_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(ForgotPasswordFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}