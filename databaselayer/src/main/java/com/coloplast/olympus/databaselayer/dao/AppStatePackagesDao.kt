package com.coloplast.olympus.databaselayer.dao

import androidx.room.*
import com.coloplast.olympus.databaselayer.entities.DbAppStatePackage
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Single

@Dao
interface AppStatePackageDao {

  @Query("DELETE FROM DbAppStatePackage")
  fun deletePackages() : Single<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAppStatePackage(dbAppStatePackage: DbAppStatePackage) : Single<Long>

  @Query("" +
      "SELECT ASP.* FROM DbAppStatePackage AS ASP " +
      "WHERE " +
      "ASP.syncState = :syncState " +
      "LIMIT :limitCount")
  fun getPendingPackages(syncState: SyncState = SyncState.Pending, limitCount: Int = 1000) : Single<List<DbAppStatePackage>>

  @Query("DELETE FROM DbAppStatePackage WHERE syncState = :syncState")
  fun deleteAlreadySynced(syncState: SyncState = SyncState.Uploaded) : Single<Int>

  @Update
  fun updatePackages(dbAppStatePackages: List<DbAppStatePackage>) : Single<Int>

  @Query("" +
      "SELECT COUNT(*) FROM DbAppStatePackage AS ASP " +
      "WHERE " +
      "ASP.syncState = :syncState " +
      "LIMIT :limitCount")
  fun pendingPackagesRemaining(syncState: SyncState = SyncState.Pending, limitCount: Int = 1000) : Single<Int>
}