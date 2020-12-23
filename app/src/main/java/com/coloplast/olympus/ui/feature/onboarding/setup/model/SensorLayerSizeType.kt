package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.types.TypeWithId

enum class SensorLayerSizeType(override val id: Int): TypeWithId {
  SIZE_40(40), SIZE_50(50), SIZE_60(60), SIZE_70(70), SIZE_80(80), UNKNOWN(0);
  
  companion object {
    fun fromId(id: Int): SensorLayerSizeType {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: UNKNOWN
    }
  }
}