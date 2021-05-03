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
  @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
  val internalAppState: String,
  @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
  val patchId: String,
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
      patchId,
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
        appState.patchId,
        appState.timestamp,
        appState.syncState
      )
    }
  }

}