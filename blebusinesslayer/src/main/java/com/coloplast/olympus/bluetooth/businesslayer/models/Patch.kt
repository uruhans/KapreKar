package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.SensorStatus

/**
 * A Patch is a description of the states that the Sensors currently have
 */
data class Patch(
  // p0
  val cutguardSensor: SensorStatus,
  // p1
  val innerSensor1: SensorStatus,
  // p2
  val innerSensor2: SensorStatus,
  // p3
  val innerSensor3: SensorStatus,
  // p4
  val wearSensor: SensorStatus,
  // p5
  val outerSensor: SensorStatus
) {
  
  // the list of inner sensors
  private val innerSensors = listOf(innerSensor1, innerSensor2, innerSensor3)
  // do we have an inner sensor breach
  val innerSensorBreach: Boolean by lazy { innerSensors.any { it == SensorStatus.BREACH } }
  // do any of the inner sensors have a state of PAY_ATTENTION
  private val innerSensorPayAttention: Boolean by lazy { innerSensors.any { it == SensorStatus.PAY_ATTENTION } }
  // a list of inner sensors that are currently in the BLINDED state
  private val innerSensorBlinded: List<SensorStatus> by lazy { innerSensors.filter { it == SensorStatus.BLINDED } }
  
  /**
   * convert the sensor states into a PatchState based on Priorities
   */
  fun getState(): PatchState {
    return when {
      // PRIORITY 7
      cutguardSensor == SensorStatus.CUT_GUARD_CUT_BROKEN -> PatchState.CUTGUARD_CUT_BROKEN
      // PRIORITY 8
      cutguardSensor == SensorStatus.CUT_GUARD_DIRT_IN_CONNECTOR -> PatchState.CUTGUARD_DIRT_IN_TRANSMITTER
      // PRIORITY 9
      outerSensor == SensorStatus.BREACH -> PatchState.OUTER_LEAK
      // PRIORITY 10
      outerSensor == SensorStatus.PAY_ATTENTION && innerSensorBreach -> PatchState.PAY_ATTENTION_10
      // PRIORITY 11
      innerSensorBreach -> PatchState.INNER_LEAK
      // PRIORITY 12
      innerSensorPayAttention || outerSensor == SensorStatus.PAY_ATTENTION -> PatchState.PAY_ATTENTION_12
      // PRIORITY 13
      outerSensor == SensorStatus.BLINDED -> PatchState.TOTAL_BLIND
      // PRIORITY 14
      innerSensorBlinded.size >= 2 -> PatchState.INNER_BLIND
      // PRIORITY 15
      else -> PatchState.OK
    }
  }
  
  fun isSameState(otherPatch: Patch) : Boolean {
    return innerSensor1.id == otherPatch.innerSensor1.id
        && innerSensor2.id == otherPatch.innerSensor2.id
        && innerSensor3.id == otherPatch.innerSensor3.id
        && outerSensor.id == otherPatch.outerSensor.id
  }
}