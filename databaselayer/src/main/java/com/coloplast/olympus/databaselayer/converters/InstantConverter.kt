package com.coloplast.olympus.databaselayer.converters

import androidx.room.TypeConverter
import java.time.Instant

object InstantConverter {

  @TypeConverter
  @JvmStatic
  fun fromInstant(value: Instant) : Long {
    return value.toEpochMilli()
  }

  @TypeConverter
  @JvmStatic
  fun toInstant(value: Long) : Instant {
    return Instant.ofEpochMilli(value)
  }
}