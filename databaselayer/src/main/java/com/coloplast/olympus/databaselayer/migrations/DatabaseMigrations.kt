package com.coloplast.olympus.databaselayer.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.coloplast.olympus.databaselayer.migrations.DbMigrationsHelper.toExisting
import com.coloplast.olympus.databaselayer.migrations.DbMigrationsHelper.toNothing

object DatabaseMigrations {

  /**
   * =======================
   * DbUser Table Change
   * =======================
   * - New column: sensorPatchSize (INTEGER)
   *
   */
  private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
      DbMigrationsHelper.alterTable(
        db = database,
        tableName = "DbUser",
        columns = mapOf(
          "id TEXT NOT NULL".toExisting(),
          "firstName TEXT".toExisting(),
          "lastName TEXT".toExisting(),
          "email TEXT".toExisting(),
          "stomaType INTEGER".toExisting(),
          "surgeryDateInterval INTEGER".toExisting(),
          "phoneNumber TEXT".toExisting(),
          "insuranceType INTEGER".toExisting(),
          "sensorPatchSize INTEGER".toNothing(), // creates the new column (and make default value NULL)
          "consent INTEGER NOT NULL".toExisting(),
          "syncState INTEGER NOT NULL".toExisting()
        ),
        primaryKeys = listOf("id")
      )
    }
  }

  val migrations : Array<Migration> = arrayOf(
    MIGRATION_3_4
  )
}