package com.coloplast.olympus.bluetooth.businesslayer.transformers

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.bluetooth.businesslayer.models.StatusVector
import com.coloplast.olympus.bluetooth.businesslayer.models.Transmitter
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

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
 *
 *
 * Sensor data (bytes 0->5) is translated into a Patch, while the rest (bytes 6->11) is translated into a Transmitter
 */
class OlympusStatusTransformer : ObservableTransformer<ByteArray, StatusVector> {
  override fun apply(upstream: Observable<ByteArray>): ObservableSource<StatusVector> {
    return upstream
        .map {
          val sensor0 = SensorStatus.fromId(it[0].toUByte())
          val sensor1 = SensorStatus.fromId(it[1].toUByte())
          val sensor2 = SensorStatus.fromId(it[2].toUByte())
          val sensor3 = SensorStatus.fromId(it[3].toUByte())
          val sensor4 = SensorStatus.fromId(it[4].toUByte())
          val sensor5 = SensorStatus.fromId(it[5].toUByte())
          
          val waterIngressDetection = WaterIngressDetection.fromId(it[6].toUByte())
          val attached = Attached.fromId(it[7].toUByte())
          val patchType = PatchType.fromId(it[8].toUByte())
          val compromised = Compromised.fromId(it[9].toUByte())
          
          val rawRotationalAngle = it[10].toInt()
          val battery = Battery.fromId(it[11].toUByte())
          
          StatusVector(
            Patch(sensor0, sensor1, sensor2, sensor3, sensor4, sensor5),
            Transmitter(waterIngressDetection, attached, patchType, compromised, rawRotationalAngle, battery)
          )
        }
  }
}