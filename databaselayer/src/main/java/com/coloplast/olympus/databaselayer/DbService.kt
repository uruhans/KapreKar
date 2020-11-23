package com.coloplast.olympus.databaselayer

import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

interface DbService {
  fun clearLocalStorage() : Completable

  fun insertOrUpdateUser(user: User): Observable<User>
  fun getUser(userId: UUID): Observable<User>
  fun getSingleUser(userId: UUID): Single<User>
  fun updateUserSyncState(userId: UUID, syncState: SyncState) : Completable

  fun getTransmitters(): Observable<DataResult<List<Transmitter>>>
  fun getTransmittersAsSingle(): Single<List<Transmitter>>
  fun getTransmitter(macAddress: String): Observable<DataResult<Transmitter>>
  fun getSingleTransmitter(macAddress: String) : Single<Transmitter>
  fun insertOrUpdateTransmitter(transmitter: Transmitter): Completable
  fun deleteTransmitter(macAddress: String): Completable
  fun deleteAllTransmitters(): Completable

  /** Transmitter Packages **/
  fun insertTransmitterPackage(transmitterPackage: TransmitterPackage): Completable
  fun getPendingTransmitterPackages(userId: UUID, packageType: PackageType) : Observable<DataResult<List<TransmitterPackage>>>
  fun deleteTransmitterPackages(packageType: PackageType) : Completable
  fun updateSyncStateForPackages(packages: List<TransmitterPackage>, syncState: SyncState) : Completable
  fun pendingPackagesRemaining(userId: UUID, packageType: PackageType) : Single<Int>

  fun getPendingTransmitters(userId: UUID) : Single<List<Transmitter>>
  fun updateSyncStateForTransmitters(transmitters: List<Transmitter>, syncState: SyncState) : Completable
}