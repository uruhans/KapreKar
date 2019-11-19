package com.coloplast.olympus.bluetooth.businesslayer.transformers

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.models.Status
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import timber.log.Timber

class OlympusStatusTransformer : ObservableTransformer<ByteArray, Status> {
  
  override fun apply(upstream: Observable<ByteArray>): ObservableSource<Status> {
    return upstream
        .map {
          val sensor0 = SensorStatus.fromId(it[0].toInt())
          val sensor1 = SensorStatus.fromId(it[1].toInt())
          val sensor2 = SensorStatus.fromId(it[2].toInt())
          val sensor3 = SensorStatus.fromId(it[3].toInt())
          val sensor4 = SensorStatus.fromId(it[4].toInt())
          val sensor5 = SensorStatus.fromId(it[5].toInt())
          
          val waterIngressDetection = WaterIngressDetection.fromId(it[6].toInt())
          val attached = Attached.fromId(it[7].toInt())
          val patchType = PatchType.fromId(it[8].toInt())
          val compromised = Compromised.fromId(it[9].toInt())
          
          val rawRotationAngle = it[10].toInt()
          Timber.d("rawRotationAngle $rawRotationAngle")
          
          val rotationalAngle = if (rawRotationAngle != -1) rawRotationAngle.times(360).div(128) else rawRotationAngle
          val battery = Battery.fromId(it[11].toInt())
          
          Status(
            sensor0,
            sensor1,
            sensor2,
            sensor3,
            sensor4,
            sensor5,
            waterIngressDetection,
            attached,
            patchType,
            compromised,
            rotationalAngle,
            battery
          )
        }
  }
}