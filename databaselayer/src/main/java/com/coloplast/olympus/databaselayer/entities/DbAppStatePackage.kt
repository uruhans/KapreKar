package com.coloplast.olympus.databaselayer.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coloplast.olympus.shared.models.AppStatePackage
import com.coloplast.olympus.shared.types.SyncState
import java.time.Instant

@Entity
class DbAppStatePackage(
  @PrimaryKey val id: Int?,
  @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
  val appState: String,
  val internalAppState: String,
  @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
  val timestamp: Instant,
  @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
  var syncState: SyncState
) {

  fun toAppStatePackage(): AppStatePackage {
    return AppStatePackage(
      id,
      appState,
      internalAppState,
      timestamp,
      syncState
    )
  }

  companion object {

    fun fromAppStatePackage(appStatePackage: AppStatePackage): DbAppStatePackage {
      return DbAppStatePackage(
        appStatePackage.id,
        appStatePackage.appState,
        appStatePackage.internalAppState,
        appStatePackage.timestamp,
        appStatePackage.syncState
      )
    }
  }

}