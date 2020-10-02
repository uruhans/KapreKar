package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

interface TransmitterComponent {

  fun getTransmitters(): Observable<DataResult<List<Transmitter>>>
  fun getTransmitter(macAddress: String): Observable<DataResult<Transmitter>>
  fun insertOrUpdateTransmitter(transmitter: Transmitter): Completable
  fun deleteTransmitter(macAddress: String): Completable
  fun deleteAllTransmitters(): Completable

  fun insertPowerPackage(macAddress: String, byteArray: ByteArray): Completable
  fun insertRawPackage(macAddress: String, byteArray: ByteArray): Completable
  fun insertStatusPackage(macAddress: String, byteArray: ByteArray): Completable

  /** Syncing with future cloud **/
  fun deletePackagesAlreadySynced(packageType: PackageType): Observable<SyncResult>
  fun syncPackagesWithCloud(userId: UUID, packageType: PackageType): Observable<SyncResult>
  fun pendingPackagesRemaining(userId: UUID, packageType: PackageType) : Single<Int>

  fun syncTransmitters(userId: UUID) : Observable<SyncResult>
  fun updateAdditionalInfoForTransmitter(macAddress: String, firmwareVersion: String, hardwareRevision: String, manufacturerName: String, serialNumber: String, settings: ByteArray, classifierSettings: ByteArray) : Completable

}