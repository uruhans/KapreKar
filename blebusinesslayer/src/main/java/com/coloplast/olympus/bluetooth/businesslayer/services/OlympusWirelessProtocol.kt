package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.bluetooth.businesslayer.models.RawDataPackage
import com.coloplast.olympus.bluetooth.businesslayer.models.StatusVector
import com.coloplast.olympus.bluetooth.businesslayer.models.Transmitter
import com.polidea.rxandroidble2.helpers.ValueInterpreter
import io.reactivex.ObservableTransformer
import timber.log.Timber

object OlympusWirelessProtocol {
  val UUID = "cc7314cc-003c-40b2-9372-4881e4805672".asUUID()
  
  object Characteristics {
    val STATUS = "b9bf73cf-a804-41b8-bf40-a87c93219d91".asUUID()
    val SETTINGS = "3dfc3caf-37fa-47c6-8ca0-b6c0ab4a8202".asUUID()
    val CLASSIFIER_SETTINGS = "3120464a-9be2-11e9-a2a3-2a2ae2dbcce4".asUUID()
    val RAW_SENSOR_DATA = "e2fa0108-b177-4e96-a550-4a47e40adcb7".asUUID()
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
   *
   *
   * Sensor data (bytes 0->5) is translated into a Patch, while the rest (bytes 6->11) is translated into a Transmitter
   */
  val statusTransformer = ObservableTransformer<ByteArray, StatusVector> { upstream ->
    upstream
      .map {
        Timber.d("{${it[0]}, ${it[1]}, ${it[2]}, ${it[3]}, ${it[4]}, ${it[5]}, ${it[6]}, ${it[7]}, ${it[8]}, ${it[9]}, ${it[10]}, ${it[11]}}")

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
  
  val rawDataPackageTransformer = ObservableTransformer<ByteArray, RawDataPackage> { upstream ->
    upstream
      .map {
        val year = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 0).toUShort()
        val month = it[2].toUByte()
        val day = it[3].toUByte()
        val hour = it[4].toUByte()
        val minute = it[5].toUByte()
        val seconds = it[6].toUByte()
        val seconds256 = it[7].toUByte()
        
        val widR = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 8).toUShort()
        val s0SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 10).toUShort()
        val s1SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 12).toUShort()
        val s2SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 14).toUShort()
        val s3SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 16).toUShort()
        val s4SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 18).toUShort()
        val s5SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 20).toUShort()
        val cs0Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 22).toUShort()
        val cs1Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 24).toUShort()
        val cs2Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 26).toUShort()
        val cs3Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 28).toUShort()
        val cs4Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 30).toUShort()
        val cs5Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 32).toUShort()
        val widC = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 34).toUShort()
        
        val battMv = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 36).toUShort()
        val battLevel = it[38].toUByte()
        
        val bleDisconnected = it[39].toUByte()
        val classifierReset = it[40].toInt()
        
        val accX = it[41].toInt()
        val accY = it[42].toInt()
        val accZ = it[43].toInt()
        
        val activity = it[44].toUByte()
        val wid = it[45].toInt()
        val attach = it[46].toInt()
        val patchId = it[47].toInt()
        val compromised = it[48].toInt()
        
        val angleRaw = it[49].toInt()
        val battPower = it[50].toInt()
        
        RawDataPackage(year, month, day, hour, minute, seconds, seconds256,
          widR, s0SensorData, s1SensorData, s2SensorData, s3SensorData, s4SensorData, s5SensorData,
          cs0Data, cs1Data, cs2Data, cs3Data, cs4Data, cs5Data, widC,
          battMv, battLevel, bleDisconnected, classifierReset, accX, accY, accZ,
          activity, wid, attach, patchId, compromised, angleRaw, battPower)
      }
  }
}