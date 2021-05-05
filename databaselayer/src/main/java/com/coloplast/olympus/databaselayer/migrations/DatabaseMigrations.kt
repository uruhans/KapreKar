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
  val MIGRATION_3_4 = object : Migration(3, 4) {
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

      database.execSQL("UPDATE DbUser SET sensorPatchSize = 0")
    }
  }

  val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
      /* no-op */
    }
  }

  /**
   * =======================
   * DbAppState Table Change
   * =======================
   * - New column: patchId (TEXT)
   *
   */
  val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
      DbMigrationsHelper.alterTable(
        db = database,
        tableName = "DbAppState",
        columns = mapOf(
          "id INTEGER".toExisting(),
          "appState TEXT NOT NULL".toExisting(),
          "internalAppState TEXT NOT NULL".toExisting(),
          "patchId TEXT".toNothing(),     // creates the new column (and make default value NULL)
          "timestamp INTEGER NOT NULL".toExisting(),
          "syncState INTEGER NOT NULL".toExisting()
        ),
        primaryKeys = listOf("id")
      )
    }
  }

  /**
   * =======================
   * DbUser Table Change
   * =======================
   * - New column: appVersion (TEXT)
   * - New column: platform (TEXT)
   *
   */
  val MIGRATION_6_7 = object : Migration(6, 7) {
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
          "sensorPatchSize INTEGER".toExisting(),
          "consent INTEGER NOT NULL".toExisting(),
          "appVersion TEXT".toNothing(),  // creates the new column (and make default value NULL)
          "platform TEXT".toNothing(),    // creates the new column (and make default value NULL)
          "syncState INTEGER NOT NULL".toExisting()
        ),
        primaryKeys = listOf("id")
      )
    }
  }

  /**
   * =======================
   * DbUser Table Change
   * =======================
   * - New column: country (TEXT)
   *
   * - UPDATE new Column with first 3 letters of phoneNumber (ie: +45)
   */
  val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
      DbMigrationsHelper.alterTable(
        db = database,
        tableName = "DbUser",
        columns = mapOf(
          "id TEXT NOT NULL".toExisting(),
          "firstName TEXT".toExisting(),
          "lastName TEXT".toExisting(),
          "email TEXT".toExisting(),
          "country TEXT".toNothing(), // creates the new column (and make default value NULL)
          "stomaType INTEGER".toExisting(),
          "surgeryDateInterval INTEGER".toExisting(),
          "phoneNumber TEXT".toExisting(),
          "insuranceType INTEGER".toExisting(),
          "sensorPatchSize INTEGER".toExisting(),
          "consent INTEGER NOT NULL".toExisting(),
          "appVersion TEXT".toExisting(),
          "platform TEXT".toExisting(),
          "syncState INTEGER NOT NULL".toExisting()
        ),
        primaryKeys = listOf("id")
      )

      database.execSQL("UPDATE DbUser SET country = (SELECT SUBSTR(phoneNumber, 1, 3) FROM DbUser)")
    }
  }

  val migrations : Array<Migration> = arrayOf(
    MIGRATION_3_4,
    MIGRATION_4_5,
    MIGRATION_5_6,
    MIGRATION_6_7,
    MIGRATION_7_8,
  )
}