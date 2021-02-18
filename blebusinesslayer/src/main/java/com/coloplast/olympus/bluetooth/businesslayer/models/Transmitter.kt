package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.shared.moshi.MoshiSerializable

/**
 * A Transmitter is a description of the states that relates specifically to the transmitter
 */
data class Transmitter(
  val waterIngressDetection: WaterIngressDetection,
  val attached: Attached,
  val patchType: PatchType,
  val compromised: Int,
  val rawRotationalAngle: Int,
  val batteryState: BatteryState,
  val fwWarning: Int,
  val fwError: Int
) : MoshiSerializable {
  
  /**
   * convert the transmitter states into a TransmitterState
   */
  fun getState(): TransmitterState {
    return when {
      waterIngressDetection == WaterIngressDetection.WATER_INGRESS -> TransmitterState.WATER_INGRESS_DETECTION
      batteryState == BatteryState.SHUTTING_DOWN -> TransmitterState.BATTERY_SHUTTING_DOWN
      batteryState == BatteryState.CHARGING -> TransmitterState.BATTERY_CHARGING
      attached == Attached.DETACHED -> TransmitterState.DETACHED
      waterIngressDetection == WaterIngressDetection.OK && (attached != Attached.UNKNOWN && attached != Attached.DETACHED) -> TransmitterState.OK
      else -> TransmitterState.UNKNOWN
    }
  }
}