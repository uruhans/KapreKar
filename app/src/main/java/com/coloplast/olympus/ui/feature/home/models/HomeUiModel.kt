package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.RawDataPackage
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.ui.common.models.UiModel
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import org.threeten.bp.LocalDateTime

sealed class HomeUiModel : UiModel {
  
  data class BluetoothIssue(val bluetoothState: BluetoothState) : HomeUiModel()
  data class ScannerResult(val scanResult: ScanResult) : HomeUiModel()
  data class StatusUpdate(
    val state: UiState = UiState.NO_CHANGE,
    val overlayState: OverlayState = OverlayState.NONE,
    val bleDevice: BleDevice
  ) : HomeUiModel()
  
  data class RawDataPackageUpdate(val macAddress: String, val rawDataPackage: RawDataPackage,
    val lastUpdate: LocalDateTime = LocalDateTime.now()) : HomeUiModel()
  
  data class ConnectedDevicesUpdate(val list: List<BleDevice>) : HomeUiModel()
  
  data class GattError(val state: State = State.Exception) : HomeUiModel() {
    enum class State {
      TimeOutException,
      Exception
    }
  }
  
  data class Disconnected(val disconnected: Boolean = true) : HomeUiModel()
  data class Error(val state: State = State.Unknown) : HomeUiModel() {
    enum class State {
      Unknown
    }
  }
}