package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.fp1.EmailFP1Controller
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.fp2.SecurityCodeFP2Controller
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.fp3.PasswordFP3Controller


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ForgotPasswordFactoryImpl: FlowFactory<ForgotPasswordModel>() {

  private lateinit var model: ForgotPasswordModel

  override fun initialize() {
    model = ForgotPasswordModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when(ForgotPasswordFlow.fromId(currentIndex)){
      ForgotPasswordFlow.ForgotPasswordFP1 -> EmailFP1Controller.getRouterTransaction()
      ForgotPasswordFlow.ForgotPasswordFP2 -> SecurityCodeFP2Controller.getRouterTransaction(model.email)
      ForgotPasswordFlow.ForgotPasswordFP3 -> PasswordFP3Controller.getRouterTransaction()
      ForgotPasswordFlow.ForgotPasswordFP4 -> GenericConfirmationController.getRouterTransaction(
        titleId = R.string.onboarding_fp4_title,
        descriptionId = R.string.onboarding_fp4_description,
        buttonId = R.string.onboarding_fp4_button_text
      )
      else -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    when(ForgotPasswordFlow.fromId(currentIndex)){
      ForgotPasswordFlow.ForgotPasswordFP1 -> {
        val email = result as String
        model.email = email
      }
      ForgotPasswordFlow.ForgotPasswordFP2 -> {
        val code = result as String
        model.code = code
      }
      ForgotPasswordFlow.ForgotPasswordFP3 -> {
        val password = result as String
        model.newPassword = password
      }
    }
  }

  override fun getData(): ForgotPasswordModel = model
}