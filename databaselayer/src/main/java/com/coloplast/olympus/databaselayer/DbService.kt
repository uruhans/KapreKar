package com.coloplast.olympus.databaselayer

import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.models.User
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

interface DbService {
  fun insertOrUpdateUser(user: User) : Observable<User>
  fun getUser(userId: UUID) : Observable<User>

  fun getTransmitters(): Observable<DataResult<List<Transmitter>>>
  fun getTransmitter(macAddress: String): Observable<DataResult<Transmitter>>
  fun insertOrUpdateTransmitter(transmitter: Transmitter): Completable
  fun deleteTransmitter(macAddress: String): Completable
  fun deleteAllTransmitters(): Completable
}