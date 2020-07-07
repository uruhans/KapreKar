package com.coloplast.olympus.ui.feature.onboarding.createprofile.types

import androidx.annotation.StringRes
import com.coloplast.olympus.R


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class HealthInsurance(@StringRes val stringId: Int) {
  HEALTH_INSURANCE_1(R.string.onboarding_health_insurance_1),
  HEALTH_INSURANCE_2(R.string.onboarding_health_insurance_2),
  HEALTH_INSURANCE_3(R.string.onboarding_health_insurance_3),
  NA(R.string.onboarding_health_insurance_4);

  companion object {
    fun fromOrdinal(ordinal: Int): HealthInsurance {
      return values()
        .associateBy { type: HealthInsurance -> type.ordinal }[ordinal] ?: NA
    }
  }
}