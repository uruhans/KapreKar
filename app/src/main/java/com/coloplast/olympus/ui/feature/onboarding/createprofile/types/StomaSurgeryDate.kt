package com.coloplast.olympus.ui.feature.onboarding.createprofile.types

import androidx.annotation.StringRes
import com.coloplast.olympus.R


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 02/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class StomaSurgeryDate(@StringRes val stringId: Int) {
  WITHIN_SIX_MONTHS(R.string.onboarding_stoma_surgery_date_within_six_months),
  MORE_THAN_SIX_MONTHS(R.string.onboarding_stoma_surgery_date_more_than_six_months),
  PRE_SURGERY(R.string.onboarding_stoma_surgery_date_pre_surgery),
  NA(R.string.onboarding_stoma_surgery_date_na);

  companion object {
    fun fromOrdinal(ordinal: Int): StomaSurgeryDate {
      return values()
        .associateBy { type: StomaSurgeryDate -> type.ordinal }[ordinal] ?: NA
    }
  }
}