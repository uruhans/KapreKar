package com.coloplast.olympus.ui.feature.home.rx

import com.coloplast.olympus.bluetooth.businesslayer.enums.Battery
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.ui.feature.home.models.OverlayStateModel
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class BleToOverlayStateTransformer : ObservableTransformer<BleDevice, OverlayStateModel> {
  override fun apply(upstream: Observable<BleDevice>): ObservableSource<OverlayStateModel> {
    return upstream
      .map { bleDevice ->
        val peripheral = bleDevice.peripheral
        
        when (bleDevice.isConnected() && peripheral != null) {
          true -> {
            
            val overlayState = when {
              peripheral.transmitter.battery == Battery.BATTERY_LOW -> OverlayState.BATTERY_LOW
              else -> OverlayState.NONE
            }
            
            OverlayStateModel(overlayState, bleDevice)
          }
          false -> {
            OverlayStateModel(OverlayState.NONE, bleDevice)
          }
        }
      }
  }
}