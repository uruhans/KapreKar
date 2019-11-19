package com.coloplast.olympus.bluetooth.businesslayer.transformers

import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class BatteryPowerTransformer : ObservableTransformer<ByteArray, PowerLevel> {
  override fun apply(upstream: Observable<ByteArray>): ObservableSource<PowerLevel> {
    return upstream
        .map { PowerLevel.fromPercentage(it[0].toInt()) }
  }
}