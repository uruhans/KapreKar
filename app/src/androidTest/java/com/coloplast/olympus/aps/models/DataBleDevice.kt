package com.coloplast.olympus.aps.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.models.*
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.services.OlympusWirelessProtocol
import io.reactivex.Observable

object DataBleDevice {

  fun ok(
    macAddress: String,
    connectionState: BleConnectionState = BleConnectionState.CONNECTED,
    attached: Attached = Attached.ATTACHED_ON_PATCH,
    compromised: Int
  ) : DeviceResult.DeviceUpdateResult {
    return DeviceResult.DeviceUpdateResult(BleDevice(
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
      ))
    )
  }

  fun bleDevice(macAddress: String, connectionState: BleConnectionState = BleConnectionState.CONNECTED, statusVectorBytes: ByteArray) : BleDevice {
    return Observable.just(statusVectorBytes)
      .compose(OlympusWirelessProtocol.statusTransformer)
      .map { Peripheral(it.patch, it.transmitter, PowerLevel.HIGH) }
      .map { BleDevice("Mock", macAddress, connectionState, it) }
      .blockingFirst()
  }
}