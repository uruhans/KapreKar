package com.coloplast.olympus.ui.feature.onboarding.createprofile.types

import androidx.annotation.StringRes
import com.coloplast.olympus.R


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 02/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class StomaType(@StringRes val stringId: Int) {
  COLOSTOMY(R.string.onboarding_stoma_type_colostomy),
  ILEOSTOMY(R.string.onboarding_stoma_type_ileostomy),
  UROSTOMY(R.string.onboarding_stoma_type_urostomy),
  NA(R.string.onboarding_stoma_type_na);

  companion object {
    fun fromOrdinal(ordinal: Int): StomaType {
      return values()
        .associateBy { type: StomaType -> type.ordinal }[ordinal] ?: NA
    }
  }
}