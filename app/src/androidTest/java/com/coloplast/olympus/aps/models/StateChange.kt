package com.coloplast.olympus.aps.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.bluetooth.businesslayer.models.Peripheral
import com.coloplast.olympus.bluetooth.businesslayer.models.Transmitter
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.ui.feature.statemachine.models.UiState

object StateChangeData {

  fun ok(
    macAddress: String,
    connectionState: BleConnectionState = BleConnectionState.CONNECTED,
    attached: Attached = Attached.ATTACHED_ON_PATCH,
    compromised: Int
  ): StateChange {
    return StateChange(
      UiState.OK_A,
      bleDevice = BleDevice(
        "Mock", macAddress, connectionState,
        Peripheral(
          Patch(
            SensorStatus.CUT_GUARD_OK,
            SensorStatus.OK,
            SensorStatus.OK,
            SensorStatus.OK,
            SensorStatus.OK,
            SensorStatus.OK
          ),
          Transmitter(
            WaterIngressDetection.OK,
            attached,
            PatchType.PATCH_0,
            compromised,
            0,
            BatteryState.OK,
            20,
            0
          ),
          PowerLevel.HIGH
        )
      )
    )
  }

  fun innerbreach(
    macAddress: String,
    connectionState: BleConnectionState = BleConnectionState.CONNECTED,
    attached: Attached = Attached.ATTACHED_ON_PATCH,
    compromised: Int
  ): StateChange {
    return StateChange(
      UiState.INNER_BREACH_B,
      bleDevice = BleDevice(
        "Mock", "AA:BB:CC:DD:EE:FF", connectionState,
        Peripheral(
          Patch(
            SensorStatus.CUT_GUARD_OK,
            SensorStatus.BREACH,
            SensorStatus.OK,
            SensorStatus.OK,
            SensorStatus.OK,
            SensorStatus.OK
          ),
          Transmitter(
            WaterIngressDetection.OK,
            attached,
            PatchType.PATCH_0,
            compromised,
            0,
            BatteryState.OK,
            20,
            0
          ),
          PowerLevel.HIGH
        )
      )
    )
  }
}