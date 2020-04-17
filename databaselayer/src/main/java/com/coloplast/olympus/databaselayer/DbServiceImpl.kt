package com.coloplast.olympus.databaselayer

import com.coloplast.olympus.databaselayer.entities.DbTransmitter
import com.coloplast.olympus.databaselayer.exceptions.DeleteFailedException
import com.coloplast.olympus.databaselayer.exceptions.InsertOrUpdateFailedException
import com.coloplast.olympus.databaselayer.rx.RxHelper
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import io.reactivex.Completable
import io.reactivex.Observable

class DbServiceImpl : DbService {
  
  private val db = OlympusDatabase.getDatabase(AppSettings.applicationContext)
  
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
}