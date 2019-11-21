package com.coloplast.olympus.bluetooth.businesslayer.transformers

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.models.PatchState
import com.coloplast.olympus.bluetooth.businesslayer.models.StatusVector
import com.coloplast.olympus.bluetooth.businesslayer.models.TransmitterState
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

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
            PatchState(sensor0, sensor1, sensor2, sensor3, sensor4, sensor5),
            TransmitterState(waterIngressDetection, attached, patchType, compromised, rawRotationalAngle, battery)
          )
        }
  }
}