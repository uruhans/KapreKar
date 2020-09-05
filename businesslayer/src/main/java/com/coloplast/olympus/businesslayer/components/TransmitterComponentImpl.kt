package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.datalayer.AzurePackageApiImpl
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import timber.log.Timber

class TransmitterComponentImpl(private val preferences: Preferences) : TransmitterComponent {

  private val dbService = DbServiceImpl()
  private val azureService = AzurePackageApiImpl()

  override fun getTransmitters(): Observable<DataResult<List<Transmitter>>> {
    return dbService.getTransmitters()
  }

  override fun getTransmitter(macAddress: String): Observable<DataResult<Transmitter>> {
    return dbService.getTransmitter(macAddress)
  }

  override fun insertOrUpdateTransmitter(transmitter: Transmitter): Completable {
    return dbService.insertOrUpdateTransmitter(transmitter)
  }

  override fun deleteTransmitter(macAddress: String): Completable {
    return dbService.deleteTransmitter(macAddress)
  }

  override fun deleteAllTransmitters(): Completable {
    return dbService.deleteAllTransmitters()
  }

  override fun insertPowerPackage(macAddress: String, byteArray: ByteArray): Completable {
    return insertTransmitterPackage(macAddress, byteArray, PackageType.Power)
  }

  override fun insertRawPackage(macAddress: String, byteArray: ByteArray): Completable {
    return insertTransmitterPackage(macAddress, byteArray, PackageType.Raw)
  }

  override fun insertStatusPackage(macAddress: String, byteArray: ByteArray): Completable {
    return insertTransmitterPackage(macAddress, byteArray, PackageType.Status)
  }

  private fun insertTransmitterPackage(
    macAddress: String,
    byteArray: ByteArray,
    packageType: PackageType
  ): Completable {
    val transmitterPackage = TransmitterPackage(
      transmitterId = macAddress,
      deviceId = preferences.getUniqueDeviceId(),
      patchId = preferences.getCurrentPatchId(),
      packageType = packageType,
      value = byteArray
    )

    return dbService.insertTransmitterPackage(transmitterPackage)
  }

  override fun deletePackagesAlreadySynced(packageType: PackageType): Observable<SyncResult> {
    return dbService.deleteTransmitterPackages(packageType)
      .toSingle<SyncResult> { SyncResult.Completed() }
      .toObservable()
      .onErrorReturn { SyncResult.Error(SyncResult.Error.State.DELETE_FAILED) }
  }

  override fun syncPackagesWithCloud(packageType: PackageType): Observable<SyncResult> {
    return getPendingPackages(packageType)
      .filter { it.isNotEmpty() }
      .doOnNext { Timber.d("Number of [$packageType] packages to sync: ${it.size}") }
      .flatMap { packages ->
        azureService.sendPackages(preferences.userLoggedIn, packageType, packages)
          .toSingle { true }
          .toObservable()
          .flatMap {
            dbService.updateSyncStateForPackages(packages, SyncState.Uploaded)
              .toSingle<SyncResult> { SyncResult.Completed() }
              .toObservable()
              .flatMap {
                if(it is SyncResult.Completed) {
                  dbService.pendingPackagesRemaining(packageType)
                    .toObservable()
                    .map<SyncResult> { count -> SyncResult.Completed(count) }
                }
                else {
                  Observable.just(it)
                }
              }
          }
          .doOnError { Timber.e(it) }
          .onErrorResumeNext(
            dbService.updateSyncStateForPackages(packages, SyncState.Pending)
              .toSingle<SyncResult> { SyncResult.Error() }
              .toObservable()
          )
      }
  }

  private fun getPendingPackages(packageType: PackageType): Observable<List<TransmitterPackage>> {
    return dbService.getPendingTransmitterPackages(packageType)
      .map {
        if (it is DataResult.Success<*>) {
          it.data as List<TransmitterPackage>
        }
        else {
          emptyList()
        }
      }
  }

  override fun pendingPackagesRemaining(packageType: PackageType): Flowable<Int> {
    return dbService.pendingPackagesRemaining(packageType)
  }
}