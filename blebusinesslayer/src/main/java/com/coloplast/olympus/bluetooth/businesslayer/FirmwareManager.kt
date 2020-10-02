package com.coloplast.olympus.bluetooth.businesslayer

import io.reactivex.Observable

interface FirmwareManager {
  fun serviceStateObservable() : Observable<ServiceState>
  fun checkFirmwareVersion(currentFirmwareVersion: String) : Observable<Unit>
  fun updateFirmware(macAddress: String) : Observable<Int>

  enum class ServiceState {
    STOP_SERVICE,
    START_SERVICE
  }
}