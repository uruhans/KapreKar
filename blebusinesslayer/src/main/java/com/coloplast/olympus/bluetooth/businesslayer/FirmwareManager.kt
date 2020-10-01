package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.firmware.FirmwareUpdateModel
import com.coloplast.olympus.shared.models.FirmwareResult
import io.reactivex.Observable

interface FirmwareManager {
  fun serviceCallback() : Observable<ServiceState>
  fun latestFirmwareVersion(macAddress: String): Observable<FirmwareResult>
  fun initiateFirmwareDownload(macAddress: String) : Observable<FirmwareUpdateModel>
  fun updateFirmware(macAddress: String, firmwareUpdateModel: FirmwareUpdateModel) : Observable<Int>

  enum class ServiceState {
    STOP_SERVICE,
    START_SERVICE
  }
}