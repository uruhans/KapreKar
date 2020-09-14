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
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.*

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

  override fun syncPackagesWithCloud(
    userId: UUID,
    packageType: PackageType
  ): Observable<SyncResult> {
    return getPendingPackages(userId, packageType)
      .filter { it.isNotEmpty() }
      .doOnNext { Timber.d("Number of [$packageType] packages to sync: ${it.size}") }
      .flatMap { packages ->
        azureService.sendPackages(userId, packageType, packages)
          .toSingle { true }
          .toObservable()
          .flatMap {
            dbService.updateSyncStateForPackages(packages, SyncState.Uploaded)
              .toSingle<SyncResult> { SyncResult.Completed() }
              .toObservable()
              .flatMap {
                if (it is SyncResult.Completed) {
                  dbService.pendingPackagesRemaining(userId, packageType)
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

  private fun getPendingPackages(
    userId: UUID,
    packageType: PackageType
  ): Observable<List<TransmitterPackage>> {
    return dbService.getPendingTransmitterPackages(userId, packageType)
      .map {
        if (it is DataResult.Success<*>) {
          it.data as List<TransmitterPackage>
        }
        else {
          emptyList()
        }
      }
  }

  override fun pendingPackagesRemaining(userId: UUID, packageType: PackageType): Single<Int> {
    return dbService.pendingPackagesRemaining(userId, packageType)
  }

  override fun syncTransmitters(userId: UUID): Observable<SyncResult> {
    return dbService.getPendingTransmitters(userId)
      .toObservable()
      .flatMap { transmitters ->
        azureService.sendTransmitter(userId, transmitters)
          .toSingle { true }
          .toObservable()
          .flatMap {
            dbService.updateSyncStateForTransmitters(transmitters, SyncState.Uploaded)
              .toSingle<SyncResult> { SyncResult.Completed() }
              .toObservable()
          }
          .onErrorResumeNext(
            dbService.updateSyncStateForTransmitters(transmitters, SyncState.Pending)
              .toSingle<SyncResult> { SyncResult.Error() }
              .toObservable()
          )
      }
  }
}