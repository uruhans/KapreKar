package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.bluetooth.businesslayer.models.StatusVector
import com.coloplast.olympus.bluetooth.businesslayer.models.Transmitter
import com.coloplast.olympus.shared.models.RawDataPackage
import com.coloplast.olympus.shared.transformers.OlympusWirelessProtocol
import io.reactivex.ObservableTransformer
import timber.log.Timber

object OlympusWirelessProtocol {
  val UUID = "cc7314cc-003c-40b2-9372-4881e4805672".asUUID()
  
  object Characteristics {
    val STATUS = "b9bf73cf-a804-41b8-bf40-a87c93219d91".asUUID()
    val SETTINGS = "3dfc3caf-37fa-47c6-8ca0-b6c0ab4a8202".asUUID()
    val CLASSIFIER_SETTINGS = "3120464a-9be2-11e9-a2a3-2a2ae2dbcce4".asUUID()
    val RAW_SENSOR_DATA = "e2fa0108-b177-4e96-a550-4a47e40adcb7".asUUID()

    val RESET_STATUS_DATA = byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
  }
  
  /**
   * A transformer to convert bytes coming from the BleDevice, into a StatusVector
   *
   * The StatusVector contains 12 bytes defined below
   *
   *  [Byte] - [Description]
   *
   *    0 - CutGuard Sensor Status
   *    1 - Inner Sensor 1
   *    2 - Inner Sensor 2
   *    3 - Inner Sensor 3
   *    4 - Wear Sensor
   *    5 - Outer Sensor
   *    6 - Water Ingress Detection
   *    7 - Attached
   *    8 - PatchType
   *    9 - Compromised
   *    10 - RawRotationAngle
   *    11 - Battery level (in percentage)
   *    12 - Firmware Warning (who knows?)
   *    13 - Firmware Error (who knows?)
   *
   * Sensor data (bytes 0->5) is translated into a Patch, while the rest (bytes 6->11) is translated into a Transmitter
   */
  val statusTransformer = ObservableTransformer<ByteArray, StatusVector> { upstream ->
    upstream
      .map {
        val statusPackage = OlympusWirelessProtocol.statusPackageTransformer(it)

        val sensor0 = SensorStatus.fromId(statusPackage.sensor0)
        val sensor1 = SensorStatus.fromId(statusPackage.sensor1)
        val sensor2 = SensorStatus.fromId(statusPackage.sensor2)
        val sensor3 = SensorStatus.fromId(statusPackage.sensor3)
        val sensor4 = SensorStatus.fromId(statusPackage.sensor4)
        val sensor5 = SensorStatus.fromId(statusPackage.sensor5)

        val waterIngressDetection = WaterIngressDetection.fromId(statusPackage.wid)
        val attached = Attached.fromId(statusPackage.attached)
        val patchType = PatchType.fromId(statusPackage.patchType)
        val compromised = Compromised.fromId(statusPackage.compromised)

        val rawRotationalAngle = statusPackage.angle
        val batteryState = BatteryState.fromId(statusPackage.batteryState)

        val fwWarning = statusPackage.fwWarning
        val fwError = statusPackage.fwError

        StatusVector(
          Patch(sensor0, sensor1, sensor2, sensor3, sensor4, sensor5),
          Transmitter(waterIngressDetection, attached, patchType, compromised, rawRotationalAngle, batteryState, fwWarning, fwError)
        )
      }
  }
  
  val rawDataPackageTransformer = ObservableTransformer<ByteArray, RawDataPackage> { upstream ->
    upstream.map { OlympusWirelessProtocol.rawDataPackageTransformer(it) }
  }

}