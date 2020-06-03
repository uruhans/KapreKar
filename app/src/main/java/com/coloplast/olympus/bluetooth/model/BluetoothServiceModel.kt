package com.coloplast.olympus.bluetooth.model

import com.coloplast.olympus.bluetooth.businesslayer.models.RawDataPackage
import java.time.LocalDateTime

sealed class BluetoothServiceModel {
  data class DeviceUpdate(val statusChange: StateChange) : BluetoothServiceModel()
  data class RawDataPackageUpdate(val fromActiveTx: Boolean, val macAddress: String, val rawDataPackage: RawDataPackage, val lastUpdate: LocalDateTime = LocalDateTime.now()) : BluetoothServiceModel()
  data class Disconnected(val otherTxStateChange: StateChange? = null) : BluetoothServiceModel()
  data class ScanningStarted(val ignored: Unit = Unit) : BluetoothServiceModel()
  data class Ignore(val macAddress: String): BluetoothServiceModel()
}