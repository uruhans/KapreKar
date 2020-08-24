package com.coloplast.olympus.databaselayer.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

  val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("")
    }
  }



  fun getMigrations() : List<Migration> = listOf(
    MIGRATION_1_2
  )
}