package com.coloplast.olympus.databaselayer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.coloplast.olympus.databaselayer.dao.TransmitterDao
import com.coloplast.olympus.databaselayer.entities.DbTransmitter

@Database(entities = arrayOf(DbTransmitter::class), version = 1)
abstract class OlympusDatabase : RoomDatabase() {
  
  abstract fun transmitterDao(): TransmitterDao
  
  companion object {
    @Volatile
    private var INSTANCE: OlympusDatabase? = null
    
    fun getDatabase(applicationContext: Context): OlympusDatabase {
      return INSTANCE ?: synchronized(OlympusDatabase::class) {
        Room.databaseBuilder(applicationContext, OlympusDatabase::class.java, "olympus-database")
          //.addMigrations(HaemMemoMigrations.MIGRATION_8_9)
          .fallbackToDestructiveMigration()
          .build()
          .apply {
            INSTANCE = this
          }
      }
    }
  }
}