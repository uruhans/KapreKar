package com.coloplast.olympus.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.coloplast.olympus.shared.preferences.Preferences
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class SyncTransmitterPackagesWorker(applicationContext: Context, workerParams: WorkerParameters) :
  Worker(applicationContext, workerParams), KoinComponent {

  private val syncWorker: SyncWorker by inject()
  private val preferences: Preferences by inject()

  override fun doWork(): Result {
    if(preferences.userLoggedIn.isEmpty()) {
      return Result.success()
    }

    val userId = UUID.fromString(preferences.userLoggedIn)
    return syncWorker.doSyncWork(userId)
  }
}