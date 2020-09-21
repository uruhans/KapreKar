package com.coloplast.olympus.work

import android.content.Context
import androidx.work.*
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.shared.types.PackageType
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import java.util.concurrent.TimeUnit

class SyncPowerPackagesWorker(applicationContext: Context, workerParams: WorkerParameters) :
  Worker(applicationContext, workerParams), KoinComponent {

  private val syncWorker: SyncWorker by inject()
  private val preferences: Preferences by inject()

  override fun doWork(): Result {
    if(preferences.userLoggedIn.isEmpty()) {
      return Result.success()
    }

    val userId = UUID.fromString(preferences.userLoggedIn)
    return syncWorker.doSyncWork(userId, PackageType.Power)
  }

  companion object {
    const val workName = "syncPowerPackages"

    fun createPeriodicWorker(): PeriodicWorkRequest {
      return PeriodicWorkRequestBuilder<SyncPowerPackagesWorker>(15, TimeUnit.MINUTES)
//      .setConstraints(
//        Constraints.Builder()
//        .setRequiresCharging(true)
//        .build()
//      )
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .build()
    }
  }
}