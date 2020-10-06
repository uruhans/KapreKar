package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.SystemUpdateComponent
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.datalayer.ApiServiceImpl
import com.coloplast.olympus.shared.models.FirmwareContent
import com.coloplast.olympus.shared.models.UpdateResult
import io.reactivex.Observable

class SystemUpdateComponentImpl : SystemUpdateComponent {

  private val dbService = DbServiceImpl()
  private val apiService = ApiServiceImpl()

  override fun systemUpdateAvailable(currentAppVersion: String): Observable<UpdateResult> {
    return appUpdateAvailable(currentAppVersion)
      .flatMap { appResult ->
        if (appResult is UpdateResult.AppUpdateAvailable) {
          Observable.just(appResult)
        }
        else {
          apiService.latestFirmware()
            .flatMap { firmwareContent ->
              dbService.getTransmittersAsSingle()
                .toObservable()
                .flatMapIterable { it }
                .map { transmitter -> mapToAppUpdate(firmwareContent, transmitter.firmwareVersion, currentAppVersion) }
                .toList()
                .map { listOfUpdateResults ->
                  val anyFirmwareUpdate = listOfUpdateResults.firstOrNull { it is UpdateResult.FirmwareUpdateAvailable }
                  anyFirmwareUpdate ?: UpdateResult.NoUpdate()
                }
                .toObservable()
            }
        }
      }
  }

  override fun appUpdateAvailable(currentAppVersion: String): Observable<UpdateResult> {
    return apiService.latestAppVersion()
      .map {
        if (it.version > currentAppVersion) {
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
          .map { mapToAppUpdate(it, dbTransmitter.firmwareVersion, currentAppVersion) }
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

  private fun mapToAppUpdate(firmwareContent: FirmwareContent, currentFirmwareVersion: String?, currentAppVersion: String): UpdateResult {
    if (firmwareContent.isMinimumAndroidVersion(currentAppVersion)) {
      if (firmwareContent.isNewer(currentFirmwareVersion ?: "")) {
        return UpdateResult.FirmwareUpdateAvailable(firmwareContent)
      }
    }

    return UpdateResult.NoUpdate()
  }
}