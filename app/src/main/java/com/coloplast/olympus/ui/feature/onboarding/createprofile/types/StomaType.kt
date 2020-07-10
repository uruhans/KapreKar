package com.coloplast.olympus.ui.feature.onboarding.createprofile.types

import androidx.annotation.StringRes
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 02/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class StomaType(override val id: Int, @StringRes val stringId: Int): TypeWithId {
  COLOSTOMY(0,R.string.onboarding_stoma_type_colostomy),
  ILEOSTOMY(1,R.string.onboarding_stoma_type_ileostomy),
  UROSTOMY(2,R.string.onboarding_stoma_type_urostomy),
  NA(4,R.string.onboarding_stoma_type_na);

  companion object {
    fun fromId(id: Int): StomaType{
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: throw NotImplementedError("Id $id was not found")
    }
  }
}