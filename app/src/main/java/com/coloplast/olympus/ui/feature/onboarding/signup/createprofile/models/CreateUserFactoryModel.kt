package com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.models

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel

class CreateUserFactoryModel : FlowFactoryModel {

  val user = User()
  lateinit var phoneValidationCode: String
}