package com.coloplast.olympus

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.coloplast.olympus.work.SyncTransmitterPackagesWorker
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class WorkManagerTests {

  private lateinit var appContext: Context

  @Before
  fun setup() {
    appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val config = Configuration.Builder()
      // Set log level to Log.DEBUG to make it easier to debug
      .setMinimumLoggingLevel(Log.DEBUG)
      // Use a SynchronousExecutor here to make it easier to write tests
      .setExecutor(SynchronousExecutor())
      .build()

    // Initialize WorkManager for instrumentation tests.
    WorkManagerTestInitHelper.initializeTestWorkManager(appContext, config)
  }


  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation()
        .targetContext
    assertEquals("com.coloplast.olympus.dev", appContext.packageName)
  }


  @Test
  @Throws(Exception::class)
  fun testPeriodicWork() {
    // Create request
    val request = PeriodicWorkRequestBuilder<SyncTransmitterPackagesWorker>(15, TimeUnit.MINUTES)
      .build()

    val workManager = WorkManager.getInstance(appContext)
    val testDriver = WorkManagerTestInitHelper.getTestDriver()
    // Enqueue and wait for result.
    workManager.enqueue(request).result.get()
    // Tells the testing framework the period delay is met
    testDriver?.setPeriodDelayMet(request.id)
    // Get WorkInfo and outputData
    val workInfo = workManager.getWorkInfoById(request.id).get()
    // Assert
    assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
  }
}
