package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.SensorStatus
import com.coloplast.olympus.shared.moshi.MoshiSerializable

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
) : MoshiSerializable {
  
  // the list of inner sensors
  private val innerSensors = listOf(innerSensor1, innerSensor2, innerSensor3)
  // a list of all sensors BUT the cutguard and wear
  private val allSensorsButCutGuardAndWear = listOf(innerSensors, outerSensor)

  // do we have an inner sensor breach
  val innerSensorBreach = innerSensors.any { it == SensorStatus.BREACH }
  //check if more than 1 inner sensor has breach or some sweat or is blinded
  val isInnerSensorsWet = innerSensors.filter { it == SensorStatus.BREACH || it == SensorStatus.BLINDED || it == SensorStatus.PAY_ATTENTION }.count() > 1
  //check outer sensor has breach or some sweat or is blinded
  val isOuterSensorWet =  outerSensor == SensorStatus.BREACH || outerSensor == SensorStatus.BLINDED || outerSensor == SensorStatus.PAY_ATTENTION
  // do any of the inner sensors have a state of PAY_ATTENTION
  val innerSensorPayAttention = innerSensors.any { it == SensorStatus.PAY_ATTENTION }
  // a list of inner sensors that are currently in the BLINDED state
  private val innerSensorBlinded: List<SensorStatus> = innerSensors.filter { it == SensorStatus.BLINDED }
  // figure out if *any* of the sensors are broken - if the cutguard sensor is < 17 OR 18 or any of the other sensors are set to CutGuardBroken Or CutGuardDirt
  private val isAnySensorsCutGuard = allSensorsButCutGuardAndWear.any { it == SensorStatus.CUT_GUARD_CUT_BROKEN || it == SensorStatus.CUT_GUARD_DIRT_IN_CONNECTOR }
        || (cutguardSensor.id >= 0 && cutguardSensor.id < SensorStatus.CUT_GUARD_OK.id) || cutguardSensor == SensorStatus.CUT_GUARD_CUT_BROKEN
  
  /**
   * convert the sensor states into a PatchState based on Priorities
   */
  fun getState(): PatchState {
    return when {
      // PRIORITY 7
      isAnySensorsCutGuard -> PatchState.CUTGUARD_CUT_BROKEN
      // PRIORITY 8
      cutguardSensor == SensorStatus.CUT_GUARD_DIRT_IN_CONNECTOR -> PatchState.CUTGUARD_DIRT_IN_TRANSMITTER
      // PRIORITY 9
      outerSensor == SensorStatus.BREACH -> PatchState.OUTER_LEAK
      // PRIORITY 10
      outerSensor == SensorStatus.PAY_ATTENTION && innerSensorBreach -> PatchState.PAY_ATTENTION_10
      outerSensor == SensorStatus.PAY_ATTENTION -> PatchState.PAY_ATTENTION_10
      // PRIORITY 11
      innerSensorBreach -> PatchState.INNER_LEAK
      // PRIORITY 12
      innerSensorPayAttention -> PatchState.PAY_ATTENTION_12
      // PRIORITY 13
      outerSensor == SensorStatus.BLINDED -> PatchState.TOTAL_BLIND
      // PRIORITY 14
      innerSensorBlinded.size >= 2 && outerSensor == SensorStatus.BREACH -> PatchState.INNER_BLIND_LEAK
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