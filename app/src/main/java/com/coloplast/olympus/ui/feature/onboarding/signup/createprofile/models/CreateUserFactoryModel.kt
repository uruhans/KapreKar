package com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.models

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel
import com.coloplast.olympus.utils.DeviceUtils

class CreateUserFactoryModel : FlowFactoryModel {

  val user = User(appVersion = DeviceUtils.getVersionNameAndCode())
  lateinit var phoneValidationCode: String
}