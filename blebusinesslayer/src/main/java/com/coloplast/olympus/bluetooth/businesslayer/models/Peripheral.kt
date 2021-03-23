package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.shared.moshi.MoshiSerializable

/**
 * Once we are connected the Peripheral contains the actual data from the BleDevice
 */
data class Peripheral(val patch: Patch, val transmitter: Transmitter, private val powerLevel: PowerLevel, val isBatteryUpdate: Boolean = false): MoshiSerializable {

  val battery = Battery(transmitter.batteryState, powerLevel)

  fun getStitchArray(): ByteArray {
    return byteArrayOf(
      // sensors
      SensorStatus.CUT_GUARD_OK.id.toByte(), // we always force an OK down
      patch.innerSensor1.id.toByte(),
      patch.innerSensor2.id.toByte(),
      patch.innerSensor3.id.toByte(),
      patch.wearSensor.id.toByte(),
      patch.outerSensor.id.toByte(),
      // WID
      transmitter.waterIngressDetection.id.toByte(),
      // ATTACHED
      Attached.ATTACHED_ON_PATCH.id.toByte(),
      // PatchType
      transmitter.patchType.id.toByte(),
      // Compromised
      -1,
      // rotationalAngle
      transmitter.rawRotationalAngle.toByte(),
      // batt_power
      -1,
      // firmware warning
      transmitter.fwWarning.toByte(),
      // firmware error
      transmitter.fwError.toByte()
    )
  }

  fun isResetVector() : Boolean {
    return patch.cutguardSensor == SensorStatus.UNKNOWN &&
        patch.innerSensor1 == SensorStatus.UNKNOWN &&
        patch.innerSensor2 == SensorStatus.UNKNOWN &&
        patch.innerSensor3 == SensorStatus.UNKNOWN &&
        patch.wearSensor == SensorStatus.UNKNOWN &&
        patch.outerSensor == SensorStatus.UNKNOWN &&
        transmitter.waterIngressDetection == WaterIngressDetection.UNKNOWN &&
        transmitter.attached == Attached.ATTACHED_ON_PATCH &&
        transmitter.patchType == PatchType.UNKNOWN &&
        (transmitter.compromised == -1 || transmitter.compromised == 255) &&
        transmitter.rawRotationalAngle == -1 &&
        transmitter.batteryState == BatteryState.UNKNOWN
  }
}