package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ForgotPasswordModel: FlowFactoryModel {
  lateinit var email: String
  lateinit var code: String
  lateinit var newPassword: String
}