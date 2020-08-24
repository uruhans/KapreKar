package com.coloplast.olympus.databaselayer.converters

import androidx.room.TypeConverter
import com.coloplast.olympus.shared.types.StomaType

object StomaTypeConverter {

  @TypeConverter
  @JvmStatic
  fun fromStomaType(value: StomaType?): Int? {
    return value?.id
  }

  @TypeConverter
  @JvmStatic
  fun toStomaType(value: Int?): StomaType? {
    return StomaType.fromId(value)
  }
}