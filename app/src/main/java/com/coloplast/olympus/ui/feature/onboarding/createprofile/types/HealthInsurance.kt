package com.coloplast.olympus.ui.feature.onboarding.createprofile.types

import androidx.annotation.StringRes
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class HealthInsurance(override val id: Int, @StringRes val stringId: Int): TypeWithId {
  HEALTH_INSURANCE_1(0,R.string.onboarding_health_insurance_1),
  HEALTH_INSURANCE_2(1,R.string.onboarding_health_insurance_2),
  HEALTH_INSURANCE_3(2,R.string.onboarding_health_insurance_3),
  NA(3,R.string.onboarding_health_insurance_4);

  companion object {
    fun fromId(id: Int): HealthInsurance {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: throw NotImplementedError("Id $id was not found")
    }
  }
}