package com.coloplast.olympus.databaselayer.converters

import androidx.room.TypeConverter
import java.util.*

object UUIDConverter {

  @TypeConverter
  @JvmStatic
  fun fromUUID(value: UUID): String {
    return value.toString()
  }

  @TypeConverter
  @JvmStatic
  fun toUUID(value: String): UUID {
    return UUID.fromString(value)
  }
}