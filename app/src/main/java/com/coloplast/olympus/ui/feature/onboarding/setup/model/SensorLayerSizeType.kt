package com.coloplast.olympus.ui.feature.onboarding.setup.model

import androidx.annotation.StringRes
import com.coloplast.olympus.R
import com.coloplast.olympus.shared.types.TypeWithId

enum class SensorLayerSizeType(override val id: Int, @StringRes val stringId: Int): TypeWithId {
  SIZE_40(0, R.string.onboarding_chapter_preparing_sl_size_40),
  SIZE_50(1, R.string.onboarding_chapter_preparing_sl_size_50),
  SIZE_60(2, R.string.onboarding_chapter_preparing_sl_size_60),
  SIZE_70(3, R.string.onboarding_chapter_preparing_sl_size_70),
  SIZE_80(4, R.string.onboarding_chapter_preparing_sl_size_80);
}