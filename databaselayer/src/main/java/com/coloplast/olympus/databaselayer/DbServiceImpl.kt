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

  override fun getUser(userId: UUID): Observable<User> {
    return db.userDao()
      .getById(userId)
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

  override fun getPendingTransmitterPackages(packageType: PackageType): Observable<DataResult<List<TransmitterPackage>>> {
    return db.transmitterPackageDao()
      .getPendingPackagesOfType(packageType)
      .flatMap { packages ->
        // packages.forEach { it.syncState = SyncState.Sending }
        val ids = packages.mapNotNull { it.id }

        db.transmitterPackageDao()
          .updatePackages(ids, SyncState.Sending)
          .map { packages }
          .toObservable()
      }
      .map { packages -> packages.map { it.toTransmitterPackage() } }
      .map { DataResult.Success(it) as DataResult<List<TransmitterPackage>> }
      .onErrorReturn { DataResult.Empty<List<TransmitterPackage>>() }
  }

  fun updateSyncStateForPackages(packages: List<TransmitterPackage>, syncState: SyncState) : Completable {
    val ids = packages.mapNotNull { it.id }

    return db.transmitterPackageDao()
      .updatePackages(ids, syncState)
      .ignoreElement()
  }
}