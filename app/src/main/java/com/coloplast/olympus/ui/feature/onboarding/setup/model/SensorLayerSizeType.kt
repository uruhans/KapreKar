package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.types.TypeWithId

enum class SensorLayerSizeType(override val id: Int) : TypeWithId {
  UNITIALIZED(0),
  SIZE_40(40),
  SIZE_50(50),
  SIZE_60(60),
  SIZE_70(70),
  SIZE_80(80)
}