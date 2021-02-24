package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.shared.moshi.MoshiSerializable

/**
 * a representation of a BleDevice
 *
 * if the peripheral exists it means we are currently connected to the
 */
data class BleDevice(val name: String?, val macAddress: String, var connectionState: BleConnectionState, var peripheral: Peripheral? = null) :
  MoshiSerializable {
  fun isConnectedOrConnecting(): Boolean {
    return isConnected() || connectionState == BleConnectionState.CONNECTING
  }

  fun isConnected(): Boolean {
    return connectionState == BleConnectionState.CONNECTED
  }

  fun isAttached(): Boolean {
    return peripheral?.transmitter?.attached == Attached.ATTACHED_ON_BODY || peripheral?.transmitter?.attached == Attached.ATTACHED_ON_PATCH
  }

  fun isAttachedOutsideBLE(): Boolean {
    return peripheral?.transmitter?.attached == Attached.ATTACHED_BODY_BLE || peripheral?.transmitter?.attached == Attached.ATTACHED_BLE_OFF
  }

  fun isCharging(): Boolean {
    return peripheral?.battery?.state == BatteryState.CHARGING
  }

  fun isBlocked(): Boolean {
    return isWidOrCutguardBlocked() ||
        peripheral?.transmitter?.compromised == -1 ||
        peripheral?.transmitter?.compromised == 255
  }

  fun isWidOrCutguardBlocked(): Boolean {
    return peripheral?.transmitter?.waterIngressDetection == WaterIngressDetection.WATER_INGRESS ||
        peripheral?.patch?.cutguardSensor == SensorStatus.CUT_GUARD_CUT_BROKEN ||
        peripheral?.patch?.cutguardSensor == SensorStatus.CUT_GUARD_DIRT_IN_CONNECTOR ||
        peripheral?.patch?.cutguardSensor == SensorStatus.CUT_GUARD_NOISE
  }


  fun isDetached(): Boolean {
    return peripheral?.transmitter?.attached == Attached.DETACHED
  }
}