package com.coloplast.olympus

import android.app.Application
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.coloplast.olympus.koin.koinModule
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.work.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CustomApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    AppSettings.initialize(this, BuildConfig.ServerUrl, BuildConfig.AzureUrl, BuildConfig.AzureCode)

    Timber.plant(Timber.DebugTree())

    startKoin {
      // currently a workaround for koin as not having Level.ERROR causes a runtime crash
      // TODO - remove when koin has fixed, but until then, not really an issue
      androidLogger(Level.ERROR)
      androidContext(this@CustomApplication)
      modules(koinModule)
    }

    if (BuildConfig.DEBUG) {
      scheduleWorkers()
    }
  }

  private fun scheduleWorkers() {
    val workManager = WorkManager.getInstance(this)

    // cancel all previous work
    workManager.cancelAllWork()

    val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncTransmitterPackagesWorker>(15, TimeUnit.MINUTES)
//      .setConstraints(
//        Constraints.Builder()
//        .setRequiresCharging(true)
//        .build()
//      )
      .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
      .build()

    workManager.enqueueUniquePeriodicWork(
        "SyncTransmitterPackagesWorker",
        ExistingPeriodicWorkPolicy.REPLACE,
        periodicSyncRequest
      )
  }
}