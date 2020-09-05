package com.coloplast.olympus.databaselayer.dao

import androidx.room.*
import com.coloplast.olympus.databaselayer.entities.DbTransmitterPackage
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface TransmitterPackageDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTransmitterPackage(dbTransmitterPackage: DbTransmitterPackage) : Single<Long>

  @Query("SELECT * FROM DbTransmitterPackage WHERE packageType = :packageType AND syncState = :syncState ORDER BY id ASC LIMIT 100")
  fun getPendingPackagesOfType(packageType: PackageType, syncState: SyncState = SyncState.Pending) : Single<List<DbTransmitterPackage>>

  @Query("DELETE FROM DbTransmitterPackage WHERE syncState = :syncState AND packageType = :packageType")
  fun deletePackagesAlreadySynced(packageType: PackageType, syncState: SyncState = SyncState.Uploaded) : Single<Int>

  @Update
  fun updatePackages(dbTransmitterPackages: List<DbTransmitterPackage>) : Single<Int>

  @Query("SELECT COUNT(*) FROM DbTransmitterPackage WHERE syncState = :syncState AND packageType = :packageType LIMIT 1000")
  fun pendingPackagesRemaining(packageType: PackageType, syncState: SyncState = SyncState.Pending) : Flowable<Int>
}