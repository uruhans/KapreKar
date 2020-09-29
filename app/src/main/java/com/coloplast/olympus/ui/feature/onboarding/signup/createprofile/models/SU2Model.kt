package com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.models

import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.types.StomaSurgeryDate
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.types.StomaType


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 02/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
data class SU2Model(
  val stomaType: StomaType,
  val stomaSurgeryDate: StomaSurgeryDate
)
