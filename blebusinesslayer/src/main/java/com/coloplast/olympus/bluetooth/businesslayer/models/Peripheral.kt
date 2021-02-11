package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.Attached
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.shared.moshi.MoshiSerializable

/**
 * Once we are connected the Peripheral contains the actual data from the BleDevice
 */
data class Peripheral(val patch: Patch, val transmitter: Transmitter, private val powerLevel: PowerLevel): MoshiSerializable {
  val battery = Battery(transmitter.batteryState, powerLevel)
  fun getStitchArray(): ByteArray {
    return byteArrayOf(
      // sensors
      patch.cutguardSensor.id.toByte(),
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
      -1,
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
}