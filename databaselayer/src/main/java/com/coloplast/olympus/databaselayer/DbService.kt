package com.coloplast.olympus.databaselayer

import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import io.reactivex.Completable
import io.reactivex.Observable

interface DbService {
  fun getTransmitters(): Observable<DataResult<List<Transmitter>>>
  fun insertOrUpdateTransmitter(transmitter: Transmitter): Completable
  fun deleteTransmitter(macAddress: String): Completable
  fun deleteAllTransmitters(): Completable
}