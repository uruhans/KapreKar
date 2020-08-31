package com.coloplast.olympus.databaselayer.dao

import androidx.room.*
import com.coloplast.olympus.databaselayer.entities.DbTransmitterPackage
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface TransmitterPackageDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTransmitterPackage(dbTransmitterPackage: DbTransmitterPackage) : Single<Long>

  @Query("SELECT * FROM DbTransmitterPackage WHERE packageType = :packageType AND syncState = :syncState ORDER BY id ASC LIMIT 1000")
  fun getPendingPackagesOfType(packageType: PackageType, syncState: SyncState = SyncState.Pending) : Observable<List<DbTransmitterPackage>>

  @Query("UPDATE DbTransmitterPackage SET syncState = :syncState WHERE id IN (:packages)")
  fun updatePackages(packages: List<Int>, syncState: SyncState) : Single<Long>

  @Query("DELETE FROM DbTransmitterPackage WHERE syncState = :syncState")
  fun deletePackagesAlreadySynced(syncState: SyncState = SyncState.Uploaded) : Single<Long>

}