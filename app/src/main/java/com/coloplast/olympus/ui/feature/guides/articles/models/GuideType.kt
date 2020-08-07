package com.coloplast.olympus.ui.feature.guides.articles.models

import com.coloplast.olympus.shared.types.TypeWithId

enum class GuideType(override val id: Int): TypeWithId {
  APPLICATION(0),
  APPLICATION_TIPS(1),
  CHARGING(2),
  CLEANING(3),
  CONNECTION(4),
  GET_STARTED(5),
  LEARN_MORE(6),
  DRY_APPLYING_TIPS(7),
  DRYING(8),
  CHECK_BATTERY(9),
  DELETE_TRANSMITTER(10),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: Int): GuideType {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: UNKNOWN
    }
  }
}