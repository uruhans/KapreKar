package com.coloplast.olympus.bluetooth.model

import com.coloplast.olympus.shared.models.CompromisedPackage
import com.coloplast.olympus.shared.models.RawDataPackage
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import java.time.LocalDateTime

sealed class BluetoothServiceModel {
  data class DeviceUpdate(val statusChange: StateChange) : BluetoothServiceModel()
  data class BatteryUpdate(val statusChange: StateChange) : BluetoothServiceModel()
  data class FirmwareUpdateAvailable(val statusChange: StateChange) : BluetoothServiceModel()
  data class AppUpdateAvailable(val statusChange: StateChange) : BluetoothServiceModel()
  data class RawDataPackageUpdate(val macAddress: String, val rawDataPackage: RawDataPackage, val lastUpdate: LocalDateTime = LocalDateTime.now()) : BluetoothServiceModel()
  data class CompromisedPackageUpdate(val macAddress: String, val compromisedPackage: CompromisedPackage, val lastUpdate: LocalDateTime = LocalDateTime.now()) : BluetoothServiceModel()
  data class Disconnected(val macAddress: String) : BluetoothServiceModel()
  data class ScanningStarted(val ignored: Unit = Unit) : BluetoothServiceModel()
  data class BluetoothDisabled(val state: UiState) : BluetoothServiceModel()
}