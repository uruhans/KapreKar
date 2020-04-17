package com.coloplast.olympus.bluetooth.model

import com.coloplast.olympus.bluetooth.businesslayer.models.RawDataPackage
import java.time.LocalDateTime

sealed class BluetoothServiceModel {
  data class DeviceUpdate(val statusChange: StateChange) : BluetoothServiceModel()
  data class RawDataPackageUpdate(val macAddress: String, val rawDataPackage: RawDataPackage, val lastUpdate: LocalDateTime = LocalDateTime.now()) : BluetoothServiceModel()
  data class Disconnected(val disconnected : Boolean = true) : BluetoothServiceModel()
}