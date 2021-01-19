package com.coloplast.olympus.databaselayer.dao

import androidx.room.*
import com.coloplast.olympus.databaselayer.entities.DbAppState
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Single

@Dao
interface AppStateDao {

  @Query("DELETE FROM DbAppState")
  fun deletePackages() : Single<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAppStatePackage(dbAppState: DbAppState) : Single<Long>

  @Query("" +
      "SELECT DAS.* FROM DbAppState AS DAS " +
      "WHERE " +
      "DAS.syncState = :syncState " +
      "LIMIT :limitCount")
  fun getPendingAppStates(syncState: SyncState = SyncState.Pending, limitCount: Int = 1000) : Single<List<DbAppState>>

  @Query("DELETE FROM DbAppState WHERE syncState = :syncState")
  fun deleteAppStatesAlreadySynced(syncState: SyncState = SyncState.Uploaded) : Single<Int>

  @Update
  fun updateAppStates(dbAppStates: List<DbAppState>) : Single<Int>

  @Query("" +
      "SELECT COUNT(*) FROM DbAppState AS DAS " +
      "WHERE " +
      "DAS.syncState = :syncState " +
      "LIMIT :limitCount")
  fun pendingAppStatesRemaining(syncState: SyncState = SyncState.Pending, limitCount: Int = 1000) : Single<Int>
}