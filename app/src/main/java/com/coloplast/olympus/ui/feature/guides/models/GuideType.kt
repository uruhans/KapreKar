package com.coloplast.olympus.ui.feature.guides.models

enum class GuideType {
  UNKNOWN,
  APPLICATION,
  APPLICATION_TIPS,
  CHARGING,
  CLEANING,
  CONNECTION,
  GET_STARTED,
  LEARN_MORE;
  
  companion object {
    fun fromOrdinal(ordinal: Int): GuideType {
      return values().associateBy { type: GuideType -> type.ordinal }[ordinal] ?: UNKNOWN
    }
  }
}