package com.coloplast.olympus.bluetooth.businesslayer.transformers

import com.coloplast.olympus.bluetooth.businesslayer.models.RawDataPackage
import com.polidea.rxandroidble2.helpers.ValueInterpreter
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class OlympusRawDataPackageTransformer : ObservableTransformer<ByteArray, RawDataPackage> {
  
  override fun apply(upstream: Observable<ByteArray>): ObservableSource<RawDataPackage> {
    return upstream
        .map {
          val widR = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 0).toUShort()
          val s0SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 2).toUShort()
          val s1SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 4).toUShort()
          val s2SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 6).toUShort()
          val s3SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 8).toUShort()
          val s4SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 10).toUShort()
          val s5SensorData = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 12).toUShort()
          val cs0Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 14).toUShort()
          val cs1Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 16).toUShort()
          val cs2Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 18).toUShort()
          val cs3Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 20).toUShort()
          val cs4Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 22).toUShort()
          val cs5Data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 24).toUShort()
          val widC = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 26).toUShort()
          val accX = it[28]
          val accY = it[29]
          val accZ = it[30]
          val accAngleRaw = it[31].toUByte()
          val batteryPercentage = it[32].toUByte()
          val batteryVoltageRaw = it[33].toUByte()
          
          val year = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 34).toUShort()
          val month = it[36].toUByte()
          val day = it[37].toUByte()
          val hour = it[38].toUByte()
          val minute = it[39].toUByte()
          val seconds = it[40].toUByte()
          val seconds256 = it[41].toUByte()
          
          RawDataPackage(
            widR,
            s0SensorData,
            s1SensorData,
            s2SensorData,
            s3SensorData,
            s4SensorData,
            s5SensorData,
            cs0Data,
            cs1Data,
            cs2Data,
            cs3Data,
            cs4Data,
            cs5Data,
            widC,
            accX,
            accY,
            accZ,
            accAngleRaw,
            batteryPercentage,
            batteryVoltageRaw,
            year,
            month,
            day,
            hour,
            minute,
            seconds,
            seconds256
          )
        }
  }
}