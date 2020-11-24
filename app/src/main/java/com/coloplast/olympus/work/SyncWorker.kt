package com.coloplast.olympus.work

import androidx.work.ListenableWorker
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SyncWorker(private val userComponent: UserComponent,
                 private val transmitterComponent: TransmitterComponent) {

  fun doSyncWork(forUserId: UUID) : ListenableWorker.Result {
    Timber.d("[SyncWorker] Beginning Synchronization of transmitter packages")

    var result = ListenableWorker.Result.success()

    var forceSync = false

    userComponent.getSingleLocalUser(forUserId)
      .subscribeOn(Schedulers.io())
      .map {
        // verify we have consent
        if (!it.consent) {
          throw RuntimeException("[SyncWorker] - User has not provided consent")
        }
        else {
          it
        }
      }
      .map { it.id }
      .flatMapObservable { userId ->
        // sync all packages of all types
        Observable.zip(
          syncPackages(PackageType.Raw, userId, forceSync),
          syncPackages(PackageType.Power, userId),
          syncPackages(PackageType.Status, userId),
          { _, _, _ ->
            // if we get to here, we have a success, so we ignore any results from the observables, and override with success
            ListenableWorker.Result.success()
          }
        )
      }
      .retryWhen {
        it.flatMap { throwable ->
          if (throwable is RetryException) {
            forceSync = true
            Timber.d("[SyncWorker] [${throwable.packageType}] - We still have pending packages [${throwable.tuplesRemaining}] - RETRYING")
            Observable.timer(1, TimeUnit.SECONDS)
          }
          else {
            Observable.error(throwable)
          }
        }
      }
      .onErrorReturn { ListenableWorker.Result.success() }
      // if we get an error always return a success to allow the worker to continue being enqueued on the next pass
      .doOnError { result = ListenableWorker.Result.success() }
      .blockingSubscribe { result = it }

    return result
  }

  private fun syncPackages(packageType: PackageType, userId: UUID, forceSync: Boolean = false) : Observable<ListenableWorker.Result> {
    // delete already synced packages
    return deletePackages(packageType)
      .flatMap { deleteResult ->
        if (deleteResult is SyncResult.Completed) {
          // then start the syncing
          transmitterComponent.syncPackagesWithCloud(userId, packageType, forceSync)
            .map { syncResult ->
              when (syncResult) {
                // if sync is successful
                is SyncResult.Completed -> {
                  // And we still have more rows in database
                  if (syncResult.tuplesRemaining > 0) {
                    // we throw exception so our retryWhen will catch and retry the work again
                    throw RetryException(packageType, syncResult.tuplesRemaining)
                  }
                  else {
                    // we have successfully synced all tuples
                    Timber.d("[SyncWorker] [$packageType] - we are successful")
                    ListenableWorker.Result.success()
                  }
                }
                is SyncResult.Error -> {
                  Timber.d("[SyncWorker] [$packageType] - we failed")
                  // we still return success here, allowing the work scheduler to continue the work in the next pass
                  ListenableWorker.Result.success()
                }
              }
            }
        }
        else {
          Timber.d("[SyncWorker] [$packageType] - ${(deleteResult as SyncResult.Error).state}")
          Observable.just(ListenableWorker.Result.success())
        }
      }
  }

  private fun deletePackages(packageType: PackageType): Observable<SyncResult> {
    // only delete if we are in release mode
    return if(BuildConfig.DEBUG || packageType != PackageType.Raw) {
      Observable.just(SyncResult.Completed())
    }
    else {
      transmitterComponent.deletePackagesAlreadySynced(packageType)
    }
  }
}