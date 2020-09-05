package com.coloplast.olympus.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SyncRawPackagesWorker(applicationContext: Context, workerParams: WorkerParameters) :
  Worker(applicationContext, workerParams), KoinComponent {

  private val transmitterComponent: TransmitterComponent by inject()

  override fun doWork(): Result {
    Timber.d("[SyncRawPackagesWorker] Beginning Synchronization of RAW packages")

    // default set to success
    var result = Result.success()

    // first delete all raw packages that have already been synced
    deleteRawPackages()
      .flatMap { deleteResult ->
        if (deleteResult is SyncResult.Completed) {
          syncRawPackages()
            .map { syncResult ->
              when (syncResult) {
                is SyncResult.Completed -> {
                  if (syncResult.tuplesRemaining > 0) {
                    // we have more items in our database, so retry the work again
                    throw RetryException(syncResult.tuplesRemaining)
                  }
                  else {
                    // we are successful
                    Timber.d("[SyncRawPackagesWorker] - we are successful")
                    Result.success()
                  }
                }
                is SyncResult.Error -> {
                  Timber.d("[SyncRawPackagesWorker] - we failed")
                  Result.failure()
                }
              }
            }
        }
        else {
          Timber.d("[SyncRawPackagesWorker] - ${(deleteResult as SyncResult.Error).state}")
          Observable.just(Result.failure())
        }
      }
      .retryWhen {
        it.flatMap { throwable ->
          if (throwable is RetryException) {
            Timber.d("[SyncRawPackagesWorker] - We still have pending packages [${throwable.tuplesRemaining}] - RETRYING")
            Observable.timer(10, TimeUnit.SECONDS)
          }
          else {
            Observable.error(throwable)
          }
        }
      }
//      .retryWhen {
//        transmitterComponent
//          .pendingPackagesRemaining(PackageType.Raw)
//          .filter { it > 0 }
//          .toObservable()
//      }
      .doOnError { result = Result.success() }
      .blockingSubscribe { result = it }

    // return result
    return result
  }

  private fun syncRawPackages(): Observable<SyncResult> {
    return transmitterComponent.syncPackagesWithCloud(PackageType.Raw)
      .subscribeOn(Schedulers.io())
  }

  private fun deleteRawPackages(): Observable<SyncResult> {
    return Observable.just(SyncResult.Completed(0))

//    return transmitterComponent.deletePackagesAlreadySynced(PackageType.Raw)
//      .subscribeOn(Schedulers.io())
  }

  class RetryException(val tuplesRemaining: Int) : Exception()
}