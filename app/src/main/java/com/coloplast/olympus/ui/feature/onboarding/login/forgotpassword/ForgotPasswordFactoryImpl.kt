package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory


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
      //todo
      else -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData(): ForgotPasswordModel = model
}