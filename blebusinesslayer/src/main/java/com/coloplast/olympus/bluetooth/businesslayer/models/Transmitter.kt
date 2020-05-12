package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.*

/**
 * A Transmitter is a description of the states that relates specifically to the transmitter
 */
data class Transmitter(
  val waterIngressDetection: WaterIngressDetection,
  val attached: Attached,
  val patchType: PatchType,
  val compromised: Compromised,
  val rawRotationalAngle: Int,
  val battery: Battery
) {
  
  /**
   * convert the transmitter states into a TransmitterState
   */
  fun getState(): TransmitterState {
    return when {
      waterIngressDetection == WaterIngressDetection.TRIGGERED -> TransmitterState.WATER_INGRESS_DETECTION
      battery == Battery.CHARGING -> TransmitterState.BATTERY_CHARGING
      attached == Attached.DETACHED -> TransmitterState.DETACHED
      waterIngressDetection == WaterIngressDetection.OK && (attached == Attached.ATTACHED_ON_PATCH || attached == Attached.ATTACHED_ON_BODY) -> TransmitterState.OK
      else -> TransmitterState.UNKNOWN
    }
  }
}