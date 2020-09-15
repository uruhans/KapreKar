package com.coloplast.olympus.work

import android.content.Context
import androidx.work.*
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.work.exceptions.RetryException
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SyncUserWorker(applicationContext: Context, workerParams: WorkerParameters) :
  Worker(applicationContext, workerParams), KoinComponent {

  private val userComponent: UserComponent by inject()
  private val preferences: Preferences by inject()

  override fun doWork(): Result {
    if(preferences.userLoggedIn.isEmpty()) {
      return Result.success()
    }

    Timber.d("[SyncUserWorker] - starting to sync user")

    val userId = UUID.fromString(preferences.userLoggedIn)

    var result = Result.success()

    userComponent.syncUser(userId)
      .subscribeOn(Schedulers.io())
      .map {
        if(it is SyncResult.Completed) {
          Result.success()
        }
        else {
          throw RetryException(0)
        }
      }
      .retryWhen {
        it.flatMap { throwable ->
          if (throwable is RetryException) {
            Timber.d("[SyncUserWorker] Something happened trying to sync user - RETRYING")
            Observable.timer(10, TimeUnit.SECONDS)
          }
          else {
            Observable.error(throwable)
          }
        }
      }
      .doOnError { result = Result.success() }
      .blockingSubscribe { result = it }

    return result
  }

  companion object {
    const val workName = "syncUserPackages"

    fun createPeriodicWorker(): PeriodicWorkRequest {
      return PeriodicWorkRequestBuilder<SyncUserWorker>(15, TimeUnit.MINUTES)
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