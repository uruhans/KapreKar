package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import io.reactivex.Completable
import io.reactivex.Observable

interface TransmitterComponent {
  
  fun getTransmitters(): Observable<DataResult<List<Transmitter>>>
  fun getTransmitter(macAddress: String): Observable<DataResult<Transmitter>>
  fun insertOrUpdateTransmitter(transmitter: Transmitter): Completable
  fun deleteTransmitter(macAddress: String): Completable
  fun deleteAllTransmitters(): Completable
}