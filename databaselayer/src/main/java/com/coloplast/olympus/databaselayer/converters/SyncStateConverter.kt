package com.coloplast.olympus.databaselayer.converters

import androidx.room.TypeConverter
import com.coloplast.olympus.shared.types.SyncState

object SyncStateConverter {

  @TypeConverter
  @JvmStatic
  fun fromSyncState(value: SyncState): Int {
    return value.id
  }

  @TypeConverter
  @JvmStatic
  fun toSyncState(value: Int): SyncState {
    return SyncState.fromId(value)
  }
}