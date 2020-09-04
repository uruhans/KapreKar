package com.coloplast.olympus.databaselayer.converters

import androidx.room.TypeConverter
import com.coloplast.olympus.shared.types.PackageType

object PackageTypeConverter {

  @TypeConverter
  @JvmStatic
  fun fromPackageType(value: PackageType): String {
    return value.id
  }

  @TypeConverter
  @JvmStatic
  fun toPackageType(value: String): PackageType {
    return PackageType.fromId(value)
  }
}