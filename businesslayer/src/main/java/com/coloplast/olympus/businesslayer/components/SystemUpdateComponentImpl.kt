package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.SystemUpdateComponent
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.datalayer.ApiServiceImpl
import com.coloplast.olympus.shared.models.UpdateResult
import io.reactivex.Observable
import timber.log.Timber

class SystemUpdateComponentImpl : SystemUpdateComponent {

  private val dbService = DbServiceImpl()
  private val apiService = ApiServiceImpl()

  override fun appUpdateAvailable(currentAppVersion: String): Observable<UpdateResult> {
    return apiService.latestAppVersion()
      .map {
        if(it.version > currentAppVersion) {
          return@map UpdateResult.AppUpdateAvailable(it)
        }
        return@map UpdateResult.NoUpdate()
      }
  }

  override fun firmwareUpdateAvailable(macAddress: String, currentAppVersion: String): Observable<UpdateResult> {
    return dbService.getSingleTransmitter(macAddress)
      .toObservable()
      .flatMap { dbTransmitter ->
        apiService.latestFirmware()
          .doOnNext { Timber.d("$it") }
          .map { firmware ->
            if (firmware.isMinimumAndroidVersion(currentAppVersion)) {
              if (firmware.isNewer(dbTransmitter.firmwareVersion ?: "")) {
                return@map UpdateResult.FirmwareUpdateAvailable(firmware)
              }
            }

            return@map UpdateResult.NoUpdate()
          }
      }
  }

  override fun initiateFirmwareDownload(macAddress: String, currentAppVersion: String): Observable<ByteArray> {
    return firmwareUpdateAvailable(macAddress, currentAppVersion)
      .flatMap {
        if (it is UpdateResult.FirmwareUpdateAvailable) {
          apiService.downloadFirmware(it.firmwareContent.filePath)
        }
        else {
          Observable.error(RuntimeException("No firmware to download"))
        }
      }
  }
}