package com.coloplast.olympus.bluetooth.businesslayer.models

sealed class DeviceResult {
  
  data class RawDataPackageResult(val rawDataPackage: RawDataPackage) : DeviceResult()
  data class DeviceUpdateResult(val bleDevice: BleDevice) : DeviceResult()
  
}