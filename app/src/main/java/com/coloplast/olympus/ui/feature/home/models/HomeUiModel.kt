package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.ui.common.models.UiModel
import org.threeten.bp.LocalDateTime

sealed class HomeUiModel : UiModel {
  
  data class DeviceConnection(val state: BleDevice) : HomeUiModel()
  data class BluetoothIssue(val bluetoothState: BluetoothState) : HomeUiModel()
  data class Result(val scanResult: ScanResult) : HomeUiModel()
  data class StatusUpdate(val bleDevice: BleDevice, val lastUpdate: LocalDateTime) : HomeUiModel()
  
}