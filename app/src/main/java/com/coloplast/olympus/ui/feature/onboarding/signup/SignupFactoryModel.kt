package com.coloplast.olympus.ui.feature.onboarding.signup

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SignupFactoryModel: FlowFactoryModel {
  // Data consent variables
  var skip: Boolean = false

  // Create user variables
  val user = User()
  lateinit var phoneValidationCode: String
}