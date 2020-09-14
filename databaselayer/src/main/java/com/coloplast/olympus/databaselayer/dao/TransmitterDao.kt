package com.coloplast.olympus.databaselayer.dao

import androidx.room.*
import com.coloplast.olympus.databaselayer.entities.DbTransmitter
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface TransmitterDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertOrUpdate(dbTransmitter: DbTransmitter): Single<Long>

  @Query("SELECT * FROM DbTransmitter")
  fun getTransmitters(): Observable<List<DbTransmitter>>

  @Query("SELECT * FROM DbTransmitter WHERE serialNumber = :macAddress")
  fun getTransmitter(macAddress: String): Observable<List<DbTransmitter>>

  @Query("DELETE FROM DbTransmitter WHERE serialNumber = :macAddress")
  fun deleteTransmitter(macAddress: String): Single<Int>

  @Query("DELETE FROM DbTransmitter")
  fun deleteAll(): Single<Int>

  @Query("SELECT * FROM DbTransmitter WHERE syncState = :syncState")
  fun getPendingTransmittersAsSingle(syncState: SyncState = SyncState.Pending): Single<List<DbTransmitter>>

  @Update
  fun updateTransmitters(transmitters: List<DbTransmitter>): Single<Int>
}