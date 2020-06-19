package com.coloplast.olympus.shared.types


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 11/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class Sensors{
  ALL, OUTER;

  companion object {
    fun fromOrdinal(ordinal: Int): Sensors {
      return values().associateBy { type: Sensors -> type.ordinal }[ordinal] ?: ALL
    }
  }
}