package com.coloplast.olympus.bluetooth.model

import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState

data class StateChange(val state: UiState = UiState.NO_CHANGE, val overlayState: OverlayState = OverlayState.NONE, val bleDevice: BleDevice) {

  fun getMacAddress() = bleDevice.macAddress
}