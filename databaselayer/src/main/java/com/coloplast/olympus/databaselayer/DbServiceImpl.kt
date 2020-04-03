package com.coloplast.olympus.databaselayer

import android.util.Log
import com.coloplast.olympus.databaselayer.entities.DbTransmitter
import com.coloplast.olympus.databaselayer.exceptions.DeleteFailedException
import com.coloplast.olympus.databaselayer.exceptions.InsertOrUpdateFailedException
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
  
  override fun insertOrUpdateTransmitter(transmitter: Transmitter): Completable {
    return db.transmitterDao()
      .insertOrUpdate(DbTransmitter.fromTransmitter(transmitter))
      .flatMapCompletable {
        if (it > 0) {
          Completable.complete()
        }
        else {
          Completable.error(InsertOrUpdateFailedException())
        }
      }
      .doOnError { Log.e("DbServiceImpl", it?.message ?: "error") }
  }
  
  override fun deleteTransmitter(macAddress: String): Completable {
    return db.transmitterDao()
      .deleteTransmitter(macAddress)
      .flatMapCompletable {
        if (it > 0) {
          Completable.complete()
        }
        else {
          Completable.error(DeleteFailedException())
        }
      }
  }
  
  override fun deleteAllTransmitters(): Completable {
    return db.transmitterDao()
      .deleteAll()
      .flatMapCompletable {
        if (it > 0) {
          Completable.complete()
        }
        else {
          Completable.error(DeleteFailedException())
        }
      }
  }
}