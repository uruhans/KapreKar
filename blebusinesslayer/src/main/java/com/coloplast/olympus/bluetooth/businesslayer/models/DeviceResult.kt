package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.shared.models.RawDataPackage
import com.coloplast.olympus.shared.models.UpdateResult

sealed class DeviceResult {
  
  data class DeviceUpdateResult(val bleDevice: BleDevice) : DeviceResult()
  data class FirmwareAvailable(val updateResult: UpdateResult.FirmwareUpdateAvailable, val bleDevice: BleDevice) : DeviceResult()
  data class RawDataPackageResult(val macAddress: String, val rawDataPackage: RawDataPackage) : DeviceResult()
  data class Error(val throwable: Throwable? = null) : DeviceResult()
  
}