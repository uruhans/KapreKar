package com.coloplast.olympus.work

import androidx.work.ListenableWorker
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.work.exceptions.RetryException
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SyncWorker(private val userComponent: UserComponent,
                 private val transmitterComponent: TransmitterComponent) {

  fun doSyncWork(forUserId: UUID, packageType: PackageType) : ListenableWorker.Result {
    Timber.d("[SyncWorker] Beginning Synchronization of [$packageType] packages")

    var result = ListenableWorker.Result.success()

    userComponent.getLocalUser(forUserId)
      .subscribeOn(Schedulers.io())
      .doOnNext { Timber.d("[SyncWorker] User has given consent [${it.consent ?: false}]") }
      .filter { it.consent ?: false }
      .map { it.id }
      .flatMap { userId ->
        deletePackages(packageType)
          .flatMap { deleteResult ->
            if (deleteResult is SyncResult.Completed) {
              transmitterComponent.syncPackagesWithCloud(userId, packageType)
                .map { syncResult ->
                  when (syncResult) {
                    is SyncResult.Completed -> {
                      if (syncResult.tuplesRemaining > 0) {
                        // we have more items in our database, so retry the work again
                        throw RetryException(syncResult.tuplesRemaining)
                      }
                      else {
                        // we are successful
                        Timber.d("[SyncWorker] - we are successful")
                        ListenableWorker.Result.success()
                      }
                    }
                    is SyncResult.Error -> {
                      Timber.d("[SyncWorker] - we failed")
                      ListenableWorker.Result.failure()
                    }
                  }
                }
            }
            else {
              Timber.d("[SyncWorker] - ${(deleteResult as SyncResult.Error).state}")
              Observable.just(ListenableWorker.Result.success())
            }
          }
      }
      .retryWhen {
        it.flatMap { throwable ->
          if (throwable is RetryException) {
            Timber.d("[SyncWorker] - We still have pending packages [${throwable.tuplesRemaining}] - RETRYING")
            Observable.timer(10, TimeUnit.SECONDS)
          }
          else {
            Observable.error(throwable)
          }
        }
      }
      .doOnError { result = ListenableWorker.Result.success() }
      .blockingSubscribe { result = it }

    return result
  }

  private fun deletePackages(packageType: PackageType): Observable<SyncResult> {
    // TODO - remove this when finished testing
    return Observable.just(SyncResult.Completed(0))

//    return transmitterComponent.deletePackagesAlreadySynced(packageType)
//      .subscribeOn(Schedulers.io())
  }
}