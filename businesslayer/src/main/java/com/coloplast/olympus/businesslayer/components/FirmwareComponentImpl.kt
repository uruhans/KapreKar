package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.FirmwareComponent
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.datalayer.ApiServiceImpl
import com.coloplast.olympus.shared.models.FirmwareResult
import io.reactivex.Observable
import timber.log.Timber

class FirmwareComponentImpl : FirmwareComponent {

  private val dbService = DbServiceImpl()
  private val apiService = ApiServiceImpl()

  override fun latestFirmwareVersion(macAddress: String, currentAppVersion: String): Observable<FirmwareResult> {
    return dbService.getSingleTransmitter(macAddress)
      .toObservable()
      .flatMap { dbTransmitter ->
        apiService.latestFirmware()
          .doOnNext { Timber.d("$it") }
          .map { firmware ->
            if (firmware.isMinimumAndroidVersion(currentAppVersion)) {
              if (firmware.isNewer(dbTransmitter.firmwareVersion ?: "")) {
                return@map FirmwareResult.UpdateAvailable(firmware)
              }
            }

            return@map FirmwareResult.NoUpdate()
          }
      }
  }

  override fun initiateFirmwareDownload(macAddress: String, currentAppVersion: String): Observable<ByteArray> {
    return latestFirmwareVersion(macAddress, currentAppVersion)
      .flatMap {
        if (it is FirmwareResult.UpdateAvailable) {
          apiService.downloadFirmware(it.firmwareContent.filePath)
        }
        else {
          Observable.error(RuntimeException("No firmware to download"))
        }
      }
  }
}