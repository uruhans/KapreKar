package com.coloplast.olympus.ui.feature.guides.articles.models

import com.coloplast.olympus.shared.types.TypeWithId

enum class GuideType(override val id: Int): TypeWithId {
  CHARGING(0),
  CLEANING(1),
  CONNECTION(2),
  LEARN_MORE(3),
  DRY_APPLYING_TIPS(4),
  DRYING(5),
  CHECK_BATTERY(6),
  DELETE_TRANSMITTER(7),
  UNDERSTANDING_SENSORS(8),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: Int): GuideType {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: UNKNOWN
    }
  }
}