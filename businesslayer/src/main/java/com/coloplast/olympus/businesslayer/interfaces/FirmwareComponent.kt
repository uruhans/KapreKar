package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.shared.models.FirmwareResult
import io.reactivex.Observable

interface FirmwareComponent {
  fun latestFirmwareVersion(macAddress: String, currentAppVersion: String) : Observable<FirmwareResult>
  fun initiateFirmwareDownload(macAddress: String, currentAppVersion: String) : Observable<ByteArray>
}