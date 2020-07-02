package com.coloplast.olympus.bluetooth.businesslayer

import io.reactivex.Completable
import io.reactivex.Observable

interface FirmwareManager {
  fun checkFirmwareVersion(currentFirmwareVersion: String) : Observable<Unit>
  fun updateFirmware(macAddress: String) : Completable
}