package com.coloplast.olympus.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.work.exceptions.RetryException
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SyncStatusPackagesWorker(applicationContext: Context, workerParams: WorkerParameters) :
  Worker(applicationContext, workerParams), KoinComponent {

  private val syncWorker: SyncWorker by inject()
  private val preferences: Preferences by inject()

  override fun doWork(): Result {
    val userId = UUID.fromString(preferences.userLoggedIn)
    return syncWorker.doSyncWork(userId, PackageType.Status)
  }
}