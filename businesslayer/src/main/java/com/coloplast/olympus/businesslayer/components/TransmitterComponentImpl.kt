package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.datalayer.ApiServiceImpl
import com.coloplast.olympus.datalayer.AzurePackageApiImpl
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import java.util.*

class TransmitterComponentImpl(private val preferences: Preferences) : TransmitterComponent {

  private val dbService = DbServiceImpl()
  private val apiService = ApiServiceImpl()
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

  private fun insertTransmitterPackage(macAddress: String, byteArray: ByteArray, packageType: PackageType): Completable {
    val transmitterPackage = TransmitterPackage(
      transmitterId = macAddress,
      deviceId = preferences.getUniqueDeviceId(),
      patchId = preferences.getCurrentPatchId(),
      packageType = packageType,
      value = byteArray
    )

    return dbService.insertTransmitterPackage(transmitterPackage)
  }

  override fun syncPackagesWithCloud(packageType: PackageType) : Observable<Boolean> {
    return dbService.getPendingTransmitterPackages(packageType)
      .filter{ it is DataResult.Success<*> }
      .map { it as DataResult.Success<*> }
      .map { it.data as List<TransmitterPackage> }
      .flatMap { packages ->
        azureService.sendPackages(UUID.fromString(preferences.userLoggedIn), packageType, packages)
          .andThen { dbService.updateSyncStateForPackages(packages, SyncState.Uploaded) }
          .toSingle { true }
          .toObservable()
          .doOnError { Timber.e(it) }
          .onErrorResumeNext(
            dbService.updateSyncStateForPackages(packages, SyncState.Pending)
              .toSingle { false }
              .toObservable()
          )
      }
  }
}