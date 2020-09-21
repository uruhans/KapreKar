package com.coloplast.olympus

import android.app.Application
import com.amitshekhar.DebugDB
import com.coloplast.olympus.koin.koinModule
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.work.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class CustomApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    AppSettings.initialize(this, BuildConfig.ServerUrl, BuildConfig.AzureUrl, BuildConfig.AzureCode)

    Timber.plant(Timber.DebugTree())
    Timber.d(DebugDB.getAddressLog())

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

  private fun scheduleWorkers(cancelWork: Boolean = false) {
    WorkScheduler.schedulePeriodicWork(
      this,
      SyncUserWorker.workName,
      SyncUserWorker.createPeriodicWorker(),
      cancelWork
    )

    WorkScheduler.schedulePeriodicWork(
      this,
      SyncTransmittersWorker.workName,
      SyncTransmittersWorker.createPeriodicWorker(),
      cancelWork
    )

    WorkScheduler.schedulePeriodicWork(
      this,
      SyncRawPackagesWorker.workName,
      SyncRawPackagesWorker.createPeriodicWorker(),
      cancelWork
    )

    WorkScheduler.schedulePeriodicWork(
      this,
      SyncStatusPackagesWorker.workName,
      SyncStatusPackagesWorker.createPeriodicWorker(),
      cancelWork
    )

    WorkScheduler.schedulePeriodicWork(
      this,
      SyncPowerPackagesWorker.workName,
      SyncPowerPackagesWorker.createPeriodicWorker(),
      cancelWork
    )
  }
}