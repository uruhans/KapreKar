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
          val wid_r = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 0)
          val s0_sensor_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 2)
          val s1_sensor_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 4)
          val s2_sensor_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 6)
          val s3_sensor_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 8)
          val s4_sensor_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 10)
          val s5_sensor_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 12)
          val cs0_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 14)
          val cs1_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 16)
          val cs2_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 18)
          val cs3_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 20)
          val cs4_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 22)
          val cs5_data = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 24)
          val wid_c = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 26)
          val accX = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_SINT8, 28)
          val accY = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_SINT8, 29)
          val accZ = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_SINT8, 30)
          val accAngleRaw = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 31)
          val batteryPercentage = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 32)
          val batteryVoltageRaw = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 33)
          
          val year = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 34)
          val month = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 36)
          val day = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 37)
          val hour = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 38)
          val minute = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 39)
          val seconds = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 40)
          val seconds256 = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 41)
          
          RawDataPackage(
            wid_r,
            s0_sensor_data,
            s1_sensor_data,
            s2_sensor_data,
            s3_sensor_data,
            s4_sensor_data,
            s5_sensor_data,
            cs0_data,
            cs1_data,
            cs2_data,
            cs3_data,
            cs4_data,
            cs5_data,
            wid_c,
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