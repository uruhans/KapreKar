package com.coloplast.olympus.aps.data

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.bluetooth.businesslayer.models.StatusVector
import com.coloplast.olympus.bluetooth.businesslayer.models.Transmitter

object MockVector {

  fun StatusVector.toByteArray(): ByteArray {
    return byteArrayOf(
      patch.cutguardSensor.id.toByte(),
      patch.innerSensor1.id.toByte(),
      patch.innerSensor2.id.toByte(),
      patch.innerSensor3.id.toByte(),
      patch.wearSensor.id.toByte(),
      patch.outerSensor.id.toByte(),
      transmitter.waterIngressDetection.id.toByte(),
      transmitter.attached.id.toByte(),
      transmitter.patchType.id.toByte(),
      transmitter.compromised.toByte(),
      transmitter.rawRotationalAngle.toByte(),
      transmitter.batteryState.id.toByte(),
      transmitter.fwWarning.toByte(),
      transmitter.fwError.toByte(),
    )
  }

  fun OK_VECTOR_ATTACHED(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        20,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }


  fun dynamicVector(compromised: Int, attached: Attached): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        attached,
        PatchType.PATCH_0,
        compromised,
        255,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun OK_VECTOR_NEW(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        255,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun OK_VECTOR_OLD(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        80,
        255,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun UNKNOWN_VECTOR(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.UNKNOWN,
        SensorStatus.UNKNOWN,
        SensorStatus.UNKNOWN,
        SensorStatus.UNKNOWN,
        SensorStatus.UNKNOWN,
        SensorStatus.UNKNOWN,
      ),
      Transmitter(
        WaterIngressDetection.UNKNOWN,
        Attached.ATTACHED_ON_PATCH,
        PatchType.UNKNOWN,
        255,
        255,
        BatteryState.UNKNOWN,
        0,
        0
      )
    ).toByteArray()
  }

  fun OK_VECTOR_DETACHED(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.DETACHED,
        PatchType.PATCH_0,
        0,
        20,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun PayAttention_with_InnerLeak_Vector(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.BREACH,
        SensorStatus.PAY_ATTENTION,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        20,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun ONE_INNER_LEAK_VECTOR(compromised: Int = 0): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.BREACH,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        compromised,
        20,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun ONE_INNER_LEAK_DETACHED_VECTOR(compromised: Int = 0): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.BREACH,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.DETACHED,
        PatchType.PATCH_0,
        compromised,
        20,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun TWO_INNER_LEAK_VECTOR(compromised: Int = 0): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.BREACH,
        SensorStatus.BREACH,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        compromised,
        20,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun OUTER_LEAK_VECTOR(compromised: Int = 0): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.BREACH,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        compromised,
        20,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun TW0_INNER_BlIND_VECTOR(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.BLINDED,
        SensorStatus.BLINDED,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        0,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun TOTAL_BlIND_VECTOR(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.BLINDED,
        SensorStatus.BLINDED,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.BLINDED,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        0,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun TW0_INNER_SWEAT_VECTOR(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.SOME_SWEAT,
        SensorStatus.SOME_SWEAT,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        0,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun WID_VECTOR(waterIngressDetection: WaterIngressDetection): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        waterIngressDetection,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        0,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun BROKEN_VECTOR(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_CUT_BROKEN,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        0,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun SHUTTING_DOWN_VECTOR(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        0,
        BatteryState.SHUTTING_DOWN,
        0,
        0
      )
    ).toByteArray()
  }

  fun DIRTY_VECTOR(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_DIRT_IN_CONNECTOR,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        0,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun CUTGUARD_NOISE(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_NOISE,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        0,
        BatteryState.OK,
        0,
        0
      )
    ).toByteArray()
  }

  fun CHARGING_VECTOR(): ByteArray {
    return StatusVector(
      Patch(
        SensorStatus.CUT_GUARD_OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
        SensorStatus.OK,
      ),
      Transmitter(
        WaterIngressDetection.OK,
        Attached.ATTACHED_ON_PATCH,
        PatchType.PATCH_0,
        0,
        0,
        BatteryState.CHARGING,
        0,
        0
      )
    ).toByteArray()
  }
}