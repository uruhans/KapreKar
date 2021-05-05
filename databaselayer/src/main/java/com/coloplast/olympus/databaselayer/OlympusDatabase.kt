package com.coloplast.olympus.databaselayer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coloplast.olympus.databaselayer.converters.InstantConverter
import com.coloplast.olympus.databaselayer.converters.PackageTypeConverter
import com.coloplast.olympus.databaselayer.converters.SyncStateConverter
import com.coloplast.olympus.databaselayer.converters.UUIDConverter
import com.coloplast.olympus.databaselayer.dao.AppStateDao
import com.coloplast.olympus.databaselayer.dao.TransmitterDao
import com.coloplast.olympus.databaselayer.dao.TransmitterPackageDao
import com.coloplast.olympus.databaselayer.dao.UserDao
import com.coloplast.olympus.databaselayer.entities.DbAppState
import com.coloplast.olympus.databaselayer.entities.DbTransmitter
import com.coloplast.olympus.databaselayer.entities.DbTransmitterPackage
import com.coloplast.olympus.databaselayer.entities.DbUser
import com.coloplast.olympus.databaselayer.migrations.DatabaseMigrations

@Database(entities = [DbUser::class, DbTransmitter::class, DbTransmitterPackage::class, DbAppState::class], version = 8)
@TypeConverters(value = [UUIDConverter::class, InstantConverter::class, SyncStateConverter::class, PackageTypeConverter::class])
abstract class OlympusDatabase : RoomDatabase() {
  
  abstract fun userDao(): UserDao
  abstract fun transmitterDao(): TransmitterDao
  abstract fun transmitterPackageDao(): TransmitterPackageDao
  abstract fun appStateDao(): AppStateDao

  companion object {
    @Volatile
    private var INSTANCE: OlympusDatabase? = null
    
    fun getDatabase(applicationContext: Context): OlympusDatabase {
      return INSTANCE ?: synchronized(OlympusDatabase::class) {
        Room.databaseBuilder(applicationContext, OlympusDatabase::class.java, "olympus-database")
          .addMigrations(*DatabaseMigrations.migrations)
          .fallbackToDestructiveMigration()
          .build()
          .apply {
            INSTANCE = this
          }
      }
    }
  }
}