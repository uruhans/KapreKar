package com.coloplast.olympus.databaselayer

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.coloplast.olympus.databaselayer.migrations.DatabaseMigrations
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception
import java.util.*

@RunWith(AndroidJUnit4::class)
class DbMigrationTests {

  private val TEST_DB = "olympus-database"

  // Array of all migrations
  private val ALL_MIGRATIONS = DatabaseMigrations.migrations

  @Rule @JvmField
  val helper: MigrationTestHelper = MigrationTestHelper(
    InstrumentationRegistry.getInstrumentation(),
    OlympusDatabase::class.java.canonicalName,
    FrameworkSQLiteOpenHelperFactory()
  )

  @Test
  @Throws(IOException::class)
  fun initialDbTest() {
    val db = helper.createDatabase(TEST_DB, 7).apply {
      // db has schema version 7. insert some data using SQL queries.
      // You cannot use DAO classes because they expect the latest schema.
      val userId = UUID.randomUUID().toString()

      execSQL(
        "" +
            "INSERT INTO DbUser " +
            "(Id, firstName, lastName, email, stomaType, surgeryDateInterval, phoneNumber, insuranceType, sensorPatchSize, consent, appVersion, platform, syncState) " +
            "VALUES " +
            "('${userId}', 'David', 'J', 'DJ@DJ.DJ', 0, 0, '+4512345678', 0, 0, 1, '2.0.0.1860', 'Android', 2)"
      )

      try {
        val cursor = query("SELECT * FROM DbUser")
        val id = cursor.getString(0)
        Assert.assertTrue(id == userId)
        Assert.assertTrue(cursor.count == 1)
      }
      catch (e: Exception) {

      }

      // Prepare for the next version.
      close()
    }

//    // Re-open the database with version 2 and provide
//    // MIGRATION_1_2 as the migration process.
//    db = helper.runMigrationsAndValidate(TEST_DB, 4, true, DatabaseMigrations.MIGRATION_3_4)
//
//    // MigrationTestHelper automatically verifies the schema changes,
//    // but you need to validate that the data was migrated properly.
//  }
  }
}