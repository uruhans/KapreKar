package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.fp1.EmailFP1Controller


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
      else -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData(): ForgotPasswordModel = model
}