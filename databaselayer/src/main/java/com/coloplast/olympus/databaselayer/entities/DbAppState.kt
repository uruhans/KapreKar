package com.coloplast.olympus.databaselayer.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coloplast.olympus.shared.models.AppState
import com.coloplast.olympus.shared.types.SyncState
import java.time.Instant

@Entity
class DbAppState(
  @PrimaryKey val id: Int?,
  @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
  val appState: String,
  val internalAppState: String,
  @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
  val timestamp: Instant,
  @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
  var syncState: SyncState
) {

  fun toAppStatePackage(): AppState {
    return AppState(
      id,
      appState,
      internalAppState,
      timestamp,
      syncState
    )
  }

  companion object {

    fun fromAppState(appState: AppState): DbAppState {
      return DbAppState(
        appState.id,
        appState.appState,
        appState.internalAppState,
        appState.timestamp,
        appState.syncState
      )
    }
  }

}