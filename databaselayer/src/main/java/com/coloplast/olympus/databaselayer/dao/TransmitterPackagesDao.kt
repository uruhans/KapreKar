package com.coloplast.olympus.databaselayer.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.coloplast.olympus.databaselayer.entities.DbTransmitterPackage
import io.reactivex.Single

@Dao
interface TransmitterPackageDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTransmitterPackage(dbTransmitterPackage: DbTransmitterPackage) : Single<Long>

}