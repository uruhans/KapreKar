package com.coloplast.olympus

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.*
import com.coloplast.olympus.koin.koinModule
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.utils.FileLoggingTree
import com.coloplast.olympus.work.*
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
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
    Timber.plant(FileLoggingTree(BuildConfig.EnableFileDebugging))

    startKoin {
      // currently a workaround for koin as not having Level.ERROR causes a runtime crash
      // TODO - remove when koin has fixed, but until then, not really an issue
      androidLogger(Level.ERROR)
      androidContext(this@CustomApplication)
      modules(koinModule)
    }

    ProcessLifecycleOwner
      .get()
      .lifecycle
      .addObserver(AppLifeCycleListener())

    scheduleWorkers()
    startAnalytics()
  }

  private fun scheduleWorkers() {
    val workManager = WorkManager.getInstance(this)

    // cancel all previous work
    workManager.cancelAllWork()

    val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncTransmitterPackagesWorker>(15, TimeUnit.MINUTES)
      .setConstraints(
        Constraints.Builder()
          // requires internet to sync packages to server
          .setRequiredNetworkType(NetworkType.CONNECTED)
          .build()
      )
      .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
      .build()

    workManager.enqueueUniquePeriodicWork(
      "SyncTransmitterPackagesWorker",
      ExistingPeriodicWorkPolicy.REPLACE,
      periodicSyncRequest
    )
  }

  private fun startAnalytics() {
    AppCenter.start(this, BuildConfig.AppCenterSecret, Analytics::class.java, Crashes::class.java)

    if(BuildConfig.DEBUG) {
      AppCenter.setLogLevel(Log.VERBOSE)
    }
  }
}