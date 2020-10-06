package com.coloplast.olympus.databaselayer

import com.coloplast.olympus.databaselayer.entities.DbTransmitter
import com.coloplast.olympus.databaselayer.entities.DbTransmitterPackage
import com.coloplast.olympus.databaselayer.entities.DbUser
import com.coloplast.olympus.databaselayer.exceptions.DeleteFailedException
import com.coloplast.olympus.databaselayer.exceptions.InsertOrUpdateFailedException
import com.coloplast.olympus.databaselayer.rx.RxHelper
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.*

class DbServiceImpl : DbService {

  private val db = OlympusDatabase.getDatabase(AppSettings.applicationContext)

  override fun insertOrUpdateUser(user: User): Observable<User> {
    return db.userDao()
      .insertOrUpdate(DbUser.fromUser(user))
      .flatMapCompletable { RxHelper.completeOrError(it.toInt(), InsertOrUpdateFailedException()) }
      .toSingle { user }
      .toObservable()
  }

  override fun updateUserSyncState(userId: UUID, syncState: SyncState): Completable {
    return db.userDao()
      .getSingleUserById(userId)
      .map {
        it.syncState = syncState
        it
      }
      .flatMapCompletable {
        db.userDao()
          .insertOrUpdate(it)
          .ignoreElement()
      }
  }

  override fun getUser(userId: UUID): Observable<User> {
    return db.userDao()
      .getById(userId)
      .map<User> { it.toUser() }
  }

  override fun getSingleUser(userId: UUID): Single<User> {
    return db.userDao()
      .getSingleUserById(userId)
      .map<User> { it.toUser() }
  }

  override fun getTransmitters(): Observable<DataResult<List<Transmitter>>> {
    return db.transmitterDao()
      .getTransmitters()
      .map { dbTransmitters ->
        if (dbTransmitters.isEmpty()) {
          DataResult.Empty<List<Transmitter>>()
        }
        else {
          DataResult.Success(dbTransmitters.map { it.toTransmitter() })
        }
      }
  }

  override fun getTransmittersAsSingle(): Single<List<Transmitter>> {
    return db.transmitterDao()
      .getTransmittersAsSingle()
      .map { dbTransmitters -> dbTransmitters.map { it.toTransmitter() } }
  }

  override fun getTransmitter(macAddress: String): Observable<DataResult<Transmitter>> {
    return db.transmitterDao()
      .getTransmitter(macAddress)
      .map { dbTransmitters ->
        if (dbTransmitters.isEmpty()) {
          DataResult.Empty<Transmitter>()
        }
        else {
          DataResult.Success(dbTransmitters.first().toTransmitter())
        }
      }
  }

  override fun getSingleTransmitter(macAddress: String): Single<Transmitter> {
    return db.transmitterDao()
      .getSingleTransmitter(macAddress)
      .map { it.toTransmitter() }
  }

  override fun insertOrUpdateTransmitter(transmitter: Transmitter): Completable {
    return db.transmitterDao()
      .insertOrUpdate(DbTransmitter.fromTransmitter(transmitter))
      .flatMapCompletable { RxHelper.completeOrError(it.toInt(), InsertOrUpdateFailedException()) }
  }

  override fun deleteTransmitter(macAddress: String): Completable {
    return db.transmitterDao()
      .deleteTransmitter(macAddress)
      .flatMapCompletable { RxHelper.completeOrError(it, DeleteFailedException()) }
  }

  override fun deleteAllTransmitters(): Completable {
    return db.transmitterDao()
      .deleteAll()
      .flatMapCompletable { RxHelper.completeOrError(it, DeleteFailedException()) }
  }

  override fun insertTransmitterPackage(transmitterPackage: TransmitterPackage): Completable {
    return db.transmitterPackageDao()
      .insertTransmitterPackage(DbTransmitterPackage.fromTransmitterPackage(transmitterPackage))
      .flatMapCompletable { RxHelper.completeOrError(it.toInt(), InsertOrUpdateFailedException()) }
  }

  override fun getPendingTransmitterPackages(userId: UUID, packageType: PackageType): Observable<DataResult<List<TransmitterPackage>>> {
    return db.transmitterPackageDao()
      .getPendingPackagesOfType(userId.toString(), packageType)
      .toObservable()
      .doOnNext { Timber.d("Number of pending [$packageType] packages in database: ${it.size}") }
      .filter { it.isNotEmpty() }
      .flatMap { packages ->
        packages.forEach { it.syncState = SyncState.Sending }

        db.transmitterPackageDao()
          .updatePackages(packages)
          .toObservable()
          .doOnNext { Timber.d("Number of [$packageType] packages Updated from Pending -> Sending: $it") }
          .map { packages.map { it.toTransmitterPackage() } }
          .map<DataResult<List<TransmitterPackage>>> { DataResult.Success(it) }
      }
      .onErrorReturn {
        Timber.e(it)
        DataResult.Empty<List<TransmitterPackage>>()
      }
  }

  override fun deleteTransmitterPackages(packageType: PackageType): Completable {
    return db.transmitterPackageDao()
      .deletePackagesAlreadySynced(packageType)
      .toObservable()
      .doOnNext { Timber.d("Number of [$packageType] packages deleted: $it") }
      .ignoreElements()
  }

  override fun updateSyncStateForPackages(packages: List<TransmitterPackage>, syncState: SyncState) : Completable {
    val dbList = packages.map { DbTransmitterPackage.fromTransmitterPackage(it) }
    dbList.forEach { it.syncState = syncState }

    return db.transmitterPackageDao()
      .updatePackages(dbList)
      .toObservable()
      .doOnNext { Timber.d("Number of packages updated to [$syncState]: $it") }
      .ignoreElements()
  }

  override fun pendingPackagesRemaining(userId: UUID, packageType: PackageType): Single<Int> {
    return db.transmitterPackageDao()
      .pendingPackagesRemaining(userId.toString(), packageType)
  }

  override fun getPendingTransmitters(userId: UUID): Single<List<Transmitter>> {
    return db.transmitterDao()
      .getPendingTransmittersAsSingle()
      .map { list ->
        list.forEach { dbTransmitter -> dbTransmitter.syncState = SyncState.Sending }
        list
      }
      .flatMap { list ->
        db.transmitterDao()
          .updateTransmitters(list)
          .map { list.map { dbTransmitter -> dbTransmitter.toTransmitter() } }
      }
  }

  override fun updateSyncStateForTransmitters(transmitters: List<Transmitter>, syncState: SyncState) : Completable {
    val dbTransmitters: List<DbTransmitter> = transmitters.map { DbTransmitter.fromTransmitter(it) }
    dbTransmitters.forEach { it.syncState = syncState }

    return db.transmitterDao().updateTransmitters(dbTransmitters)
      .ignoreElement()
  }
}