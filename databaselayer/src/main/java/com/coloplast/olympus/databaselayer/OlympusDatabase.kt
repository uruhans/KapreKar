package com.coloplast.olympus.databaselayer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coloplast.olympus.databaselayer.converters.*
import com.coloplast.olympus.databaselayer.dao.TransmitterDao
import com.coloplast.olympus.databaselayer.dao.UserDao
import com.coloplast.olympus.databaselayer.entities.DbTransmitter
import com.coloplast.olympus.databaselayer.entities.DbTransmitterPackages
import com.coloplast.olympus.databaselayer.entities.DbUser

@Database(entities = [DbUser::class, DbTransmitter::class, DbTransmitterPackages::class], version = 1)
@TypeConverters(value = [UUIDConverter::class, InstantConverter::class, SyncStateConverter::class, StomaTypeConverter::class, PackageTypeConverter::class])
abstract class OlympusDatabase : RoomDatabase() {
  
  abstract fun userDao(): UserDao
  abstract fun transmitterDao(): TransmitterDao

  companion object {
    @Volatile
    private var INSTANCE: OlympusDatabase? = null
    
    fun getDatabase(applicationContext: Context): OlympusDatabase {
      return INSTANCE ?: synchronized(OlympusDatabase::class) {
        Room.databaseBuilder(applicationContext, OlympusDatabase::class.java, "olympus-database")
          //.addMigrations(DatabaseMigrations.MIGRATION_1_2)
          .fallbackToDestructiveMigration()
          .build()
          .apply {
            INSTANCE = this
          }
      }
    }
  }
}