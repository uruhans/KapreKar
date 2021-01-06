package com.coloplast.olympus.ui.feature.onboarding.setup.model

import androidx.annotation.StringRes
import com.coloplast.olympus.R
import com.coloplast.olympus.shared.types.TypeWithId

enum class SensorLayerSizeType(override val id: Int, @StringRes val stringId: Int): TypeWithId {
  UNKNOWN(0, R.string.generic_error_call_support_title),
  SIZE_40(1, R.string.onboarding_chapter_preparing_sl_size_40),
  SIZE_50(2, R.string.onboarding_chapter_preparing_sl_size_50),
  SIZE_60(3, R.string.onboarding_chapter_preparing_sl_size_60),
  SIZE_70(4, R.string.onboarding_chapter_preparing_sl_size_70),
  SIZE_80(5, R.string.onboarding_chapter_preparing_sl_size_80);

  companion object {
    fun fromId(idx: Int): SensorLayerSizeType {
      return values().associateBy { enum: TypeWithId -> enum.id }[idx]
        ?: UNKNOWN
    }
  }
}