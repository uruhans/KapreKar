package com.coloplast.olympus.aps.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.bluetooth.businesslayer.models.Peripheral
import com.coloplast.olympus.bluetooth.businesslayer.models.Transmitter
import com.coloplast.olympus.bluetooth.model.StateChange

object Data {

  fun stateChange(
    connectionState: BleConnectionState = BleConnectionState.CONNECTED,
    attached: Attached = Attached.ATTACHED_ON_PATCH
  ): StateChange {
    return StateChange(
      bleDevice = BleDevice(
        "Mock", "AA:BB:CC:DD:EE:FF", connectionState,
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
            10,
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