package com.coloplast.olympus.bluetooth.model

import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.shared.moshi.MoshiSerializable
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import java.time.Instant

data class StateChange(
  var state: UiState = UiState.NO_CHANGE,
  val overlayState: OverlayState = OverlayState.NONE,
  val bleDevice: BleDevice,
  var apsAction: ApsAction = ApsAction.Nothing,
  val timestamp: Long = Instant.now().epochSecond,
  var resetOrStitchCandidate: ByteArray? = null,
  var isBatteryUpdate: Boolean = false
) : MoshiSerializable {

  fun getMacAddress() = bleDevice.macAddress

  fun secondsSinceNow(): Int {
    return (Instant.now().epochSecond - timestamp).toInt()
  }

}