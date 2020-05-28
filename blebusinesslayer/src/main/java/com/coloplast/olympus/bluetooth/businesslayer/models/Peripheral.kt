package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.shared.moshi.MoshiSerializable

/**
 * Once we are connected the Peripheral contains the actual data from the BleDevice
 */
data class Peripheral(val patch: Patch, val transmitter: Transmitter, val powerLevel: PowerLevel) : MoshiSerializable {
  fun getStitchArray(): ByteArray {
    return byteArrayOf(
      patch.cutguardSensor.id.toByte(),
      patch.innerSensor1.id.toByte(),
      patch.innerSensor2.id.toByte(),
      patch.innerSensor3.id.toByte(),
      patch.wearSensor.id.toByte(),
      patch.outerSensor.id.toByte(),
      -1,
      -1,
      -1,
      -1,
      -1,
      -1
    )
  }
}