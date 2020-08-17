package com.coloplast.olympus.ui.feature.onboarding.createprofile.models

import com.coloplast.olympus.shared.models.CreateUserModel
import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class CreateUserFactoryModel : CreateUserModel(), FlowFactoryModel {

  private val UK_PREFIX_1 = "+44"
  private val UK_PREFIX_2 = "0044"

  fun isUKNumber(): Boolean {
    return phoneNumber.contains(UK_PREFIX_1) || phoneNumber.contains(UK_PREFIX_2)
  }
}