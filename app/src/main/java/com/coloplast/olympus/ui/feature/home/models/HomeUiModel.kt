package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.ui.common.models.UiModel
import java.time.LocalDateTime

sealed class HomeUiModel : UiModel {
  
  data class BluetoothIssue(val bluetoothState: BluetoothState) : HomeUiModel()
  data class DeviceUpdate(val stateChange: StateChange) : HomeUiModel()
  data class RawDataPackageUpdate(val macAddress: String, val lastUpdate: LocalDateTime = LocalDateTime.now()) : HomeUiModel()
  data class ConnectionStateUpdate(val connectionState: BleConnectionState) : HomeUiModel()
  data class StitchOrResetComplete(val complete: Boolean = true) : HomeUiModel()
  data class Scanning(val scanning: Unit = Unit) : HomeUiModel()
  data class ScanningStopped(val scanning: Unit = Unit) : HomeUiModel()
  data class NoTransmittersFound(val anyTransmitters: Boolean = true) : HomeUiModel()
  data class Initial(val nothing: Unit = Unit) : HomeUiModel()
  data class DeviceDisconnected(private val disconnected: Unit = Unit) : HomeUiModel()
}