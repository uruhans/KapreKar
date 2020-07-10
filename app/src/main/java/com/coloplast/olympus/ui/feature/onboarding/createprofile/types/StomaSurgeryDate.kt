package com.coloplast.olympus.ui.feature.onboarding.createprofile.types

import androidx.annotation.StringRes
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 02/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class StomaSurgeryDate(override val id: Int, @StringRes val stringId: Int): TypeWithId {
  WITHIN_SIX_MONTHS(0, R.string.onboarding_stoma_surgery_date_within_six_months),
  MORE_THAN_SIX_MONTHS(1, R.string.onboarding_stoma_surgery_date_more_than_six_months),
  PRE_SURGERY(2, R.string.onboarding_stoma_surgery_date_pre_surgery),
  NA(3, R.string.onboarding_stoma_surgery_date_na);

  companion object {
    fun fromId(id: Int): StomaSurgeryDate{
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: throw NotImplementedError("Id $id was not found")
    }
  }
}