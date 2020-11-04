package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.firmware.FirmwareUpdateModel
import com.coloplast.olympus.shared.models.UpdateResult
import io.reactivex.Observable

interface FirmwareManager {
  fun firmwareUpdateAvailable(macAddress: String, currentAppVersion: String): Observable<UpdateResult>
  fun initiateFirmwareDownload(macAddress: String, currentAppVersion: String) : Observable<FirmwareUpdateModel>
  fun updateFirmware(macAddress: String, firmwareUpdateModel: FirmwareUpdateModel) : Observable<Int>
}