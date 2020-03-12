package com.coloplast.olympus.ui.feature.rx

import com.coloplast.olympus.bluetooth.businesslayer.enums.Battery
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.ui.feature.home.models.OverlayStateModel
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class BleToOverlayStateTransformer : ObservableTransformer<DeviceResult, OverlayStateModel> {
  override fun apply(upstream: Observable<DeviceResult>): ObservableSource<OverlayStateModel> {
    return upstream
      .filter { it is DeviceResult.DeviceUpdateResult }
      .map { it as DeviceResult.DeviceUpdateResult }
      .map { deviceResult ->
        val peripheral = deviceResult.bleDevice.peripheral
        
        when (deviceResult.bleDevice.isConnected() && peripheral != null) {
          true -> {
            
            val overlayState = when {
              peripheral.transmitter.battery == Battery.BATTERY_LOW -> OverlayState.BATTERY_LOW
              else -> OverlayState.NONE
            }
            
            OverlayStateModel(overlayState, deviceResult.bleDevice)
          }
          false -> {
            OverlayStateModel(OverlayState.NONE, deviceResult.bleDevice)
          }
        }
      }
  }
}