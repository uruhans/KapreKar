package com.coloplast.olympus.databaselayer.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coloplast.olympus.databaselayer.entities.DbTransmitter
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface TransmitterDao {
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertOrUpdate(dbTransmitter: DbTransmitter): Single<Long>
  
  @Query("SELECT * FROM DbTransmitter")
  fun getTransmitters(): Observable<List<DbTransmitter>>
  
  @Query("SELECT * FROM DbTransmitter WHERE id = :macAddress")
  fun getTransmitter(macAddress: String): Observable<List<DbTransmitter>>
  
  @Query("DELETE FROM DbTransmitter WHERE id = :macAddress")
  fun deleteTransmitter(macAddress: String): Single<Int>
  
  @Query("DELETE FROM DbTransmitter")
  fun deleteAll(): Single<Int>
  
}