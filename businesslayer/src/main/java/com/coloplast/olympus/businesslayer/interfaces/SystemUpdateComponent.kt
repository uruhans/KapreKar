package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.shared.models.UpdateResult
import io.reactivex.Observable

interface SystemUpdateComponent {
  fun systemUpdateAvailable(currentAppVersion: String) : Observable<UpdateResult>

  fun appUpdateAvailable(currentAppVersion: String) : Observable<UpdateResult>

  fun firmwareUpdateAvailable(macAddress: String, currentAppVersion: String) : Observable<UpdateResult>
  fun initiateFirmwareDownload(macAddress: String, currentAppVersion: String) : Observable<ByteArray>
}