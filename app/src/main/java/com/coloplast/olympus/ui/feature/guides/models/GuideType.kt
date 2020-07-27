package com.coloplast.olympus.ui.feature.guides.models

import com.coloplast.olympus.shared.types.TypeWithId

enum class GuideType(override val id: Int): TypeWithId {
  APPLICATION(0),
  APPLICATION_TIPS(1),
  CHARGING(2),
  CLEANING(3),
  DRYING(4),
  CONNECTION(5),
  GET_STARTED(6),
  LEARN_MORE(7),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: Int): GuideType {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: UNKNOWN
    }
  }
}