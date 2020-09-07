package com.coloplast.olympus.databaselayer.dao

import androidx.room.*
import com.coloplast.olympus.databaselayer.entities.DbTransmitterPackage
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface TransmitterPackageDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTransmitterPackage(dbTransmitterPackage: DbTransmitterPackage) : Single<Long>

  @Query("" +
      "SELECT TP.* FROM DbTransmitterPackage AS TP " +
      "INNER JOIN DbTransmitter AS T ON TP.transmitterId = T.id " +
      "INNER JOIN DbUser AS U ON T.userId = U.id " +
      "WHERE " +
      "U.id = :userId AND " +
      "TP.packageType = :packageType AND " +
      "TP.syncState = :syncState " +
      "ORDER BY TP.id ASC " +
      "LIMIT :limitCount")
  fun getPendingPackagesOfType(userId: String, packageType: PackageType, syncState: SyncState = SyncState.Pending, limitCount: Int = 1000) : Single<List<DbTransmitterPackage>>

  @Query("DELETE FROM DbTransmitterPackage WHERE syncState = :syncState AND packageType = :packageType")
  fun deletePackagesAlreadySynced(packageType: PackageType, syncState: SyncState = SyncState.Uploaded) : Single<Int>

  @Update
  fun updatePackages(dbTransmitterPackages: List<DbTransmitterPackage>) : Single<Int>

  @Query("" +
      "SELECT COUNT(*) FROM DbTransmitterPackage AS TP " +
      "INNER JOIN DbTransmitter AS T ON TP.transmitterId = T.id " +
      "INNER JOIN DbUser AS U ON T.userId = U.id " +
      "WHERE " +
      "U.id = :userId AND " +
      "TP.packageType = :packageType AND " +
      "TP.syncState = :syncState " +
      "ORDER BY TP.id ASC " +
      "LIMIT :limitCount")
  fun pendingPackagesRemaining(userId: String, packageType: PackageType, syncState: SyncState = SyncState.Pending, limitCount: Int = 1000) : Flowable<Int>
}