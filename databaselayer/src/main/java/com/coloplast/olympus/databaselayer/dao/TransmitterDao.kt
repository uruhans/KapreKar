package com.coloplast.olympus.databaselayer.dao

import androidx.room.*
import com.coloplast.olympus.databaselayer.entities.DbTransmitter
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface TransmitterDao {

  @Query("DELETE FROM DbTransmitter")
  fun deleteTransmitters() : Single<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertOrUpdate(dbTransmitter: DbTransmitter): Single<Long>

  @Query("SELECT * FROM DbTransmitter")
  fun getTransmitters(): Observable<List<DbTransmitter>>

  @Query("SELECT * FROM DbTransmitter")
  fun getTransmittersAsSingle() : Single<List<DbTransmitter>>

  @Query("SELECT * FROM DbTransmitter WHERE id = :macAddress")
  fun getTransmitter(macAddress: String): Observable<List<DbTransmitter>>

  @Query("SELECT * FROM DbTransmitter WHERE id = :macAddress")
  fun getSingleTransmitter(macAddress: String): Single<DbTransmitter>

  @Query("DELETE FROM DbTransmitter WHERE id = :macAddress")
  fun deleteTransmitter(macAddress: String): Single<Int>

  @Query("DELETE FROM DbTransmitter")
  fun deleteAll(): Single<Int>

  @Query("SELECT * FROM DbTransmitter WHERE syncState = :syncState")
  fun getPendingTransmittersAsSingle(syncState: SyncState = SyncState.Pending): Single<List<DbTransmitter>>

  @Update
  fun updateTransmitters(transmitters: List<DbTransmitter>): Single<Int>
}