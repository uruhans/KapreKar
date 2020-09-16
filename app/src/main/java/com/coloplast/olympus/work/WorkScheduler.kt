package com.coloplast.olympus.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager

object WorkScheduler {

  fun schedulePeriodicWork(applicationContext: Context, uniqueWorkName: String, workRequest: PeriodicWorkRequest, cancelWork: Boolean = false) {
    if (cancelWork) {
      WorkManager.getInstance(applicationContext)
        .cancelUniqueWork(uniqueWorkName)
    }

    WorkManager.getInstance(applicationContext)
      .enqueueUniquePeriodicWork(
        uniqueWorkName,
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
      )
  }
}