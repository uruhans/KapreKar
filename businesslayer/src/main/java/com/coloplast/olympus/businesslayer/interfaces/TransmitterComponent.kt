package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Completable
import io.reactivex.Observable

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
  fun syncPackagesWithCloud(packageType: PackageType) : Observable<Boolean>
}