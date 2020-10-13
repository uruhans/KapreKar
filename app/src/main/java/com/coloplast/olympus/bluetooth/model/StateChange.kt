package com.coloplast.olympus.bluetooth.model

import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.shared.moshi.MoshiSerializable
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState

data class StateChange(val state: UiState = UiState.NO_CHANGE, val overlayState: OverlayState = OverlayState.NONE, val bleDevice: BleDevice, var patchProbability: PatchProbability = PatchProbability.NOT_SET) : MoshiSerializable {

  fun getMacAddress() = bleDevice.macAddress
}