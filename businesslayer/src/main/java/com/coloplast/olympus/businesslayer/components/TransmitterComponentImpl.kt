package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import io.reactivex.Completable
import io.reactivex.Observable

class TransmitterComponentImpl : TransmitterComponent {
  
  private val dbService = DbServiceImpl()
  
  override fun getTransmitters(): Observable<DataResult<List<Transmitter>>> {
    return dbService.getTransmitters()
  }
  
  override fun getTransmitter(macAddress: String): Observable<DataResult<Transmitter>> {
    return dbService.getTransmitter(macAddress)
  }
  
  override fun insertOrUpdateTransmitter(transmitter: Transmitter): Completable {
    return dbService.insertOrUpdateTransmitter(transmitter)
  }
  
  override fun deleteTransmitter(macAddress: String): Completable {
    return dbService.deleteTransmitter(macAddress)
  }
  
  override fun deleteAllTransmitters(): Completable {
    return dbService.deleteAllTransmitters()
  }
  
}