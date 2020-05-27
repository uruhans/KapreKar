package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.BatteryState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.businesslayer.models.PatchState
import com.coloplast.olympus.bluetooth.businesslayer.models.TransmitterState
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState

object StateTransformers {
  fun bleToUiState(deviceResult: DeviceResult.DeviceUpdateResult): UiStateModel {
    val peripheral = deviceResult.bleDevice.peripheral
    
    return when (deviceResult.bleDevice.isConnected() && peripheral != null) {
      true -> {
        val uiState = when (peripheral.transmitter.getState()) {
          TransmitterState.WATER_INGRESS_DETECTION -> UiState.WATER_INGRESS_DETECTION_J
          TransmitterState.DETACHED -> UiState.TRANSMITTER_DETACHED_V
          TransmitterState.BATTERY_CHARGING -> UiState.TRANSMITTER_BATTERY_CHARGING_W
          TransmitterState.OK -> {
            when (peripheral.patch.getState()) {
              PatchState.OK -> UiState.OK_A
              PatchState.INNER_LEAK -> UiState.INNER_BREACH_B
              PatchState.OUTER_LEAK -> UiState.OUTER_BREACH_C
              PatchState.INNER_BLIND -> UiState.INNER_BLIND_D
              PatchState.TOTAL_BLIND -> UiState.TOTAL_BLIND_E
              PatchState.PAY_ATTENTION_10,
              PatchState.PAY_ATTENTION_12 -> UiState.PAY_ATTENTION_F
              PatchState.CUTGUARD_CUT_BROKEN -> UiState.CUTGUARD_BROKEN_I
              PatchState.CUTGUARD_DIRT_IN_TRANSMITTER -> UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K
              else -> UiState.SOMETHING_ELSE
            }
          }
          else -> UiState.SOMETHING_ELSE
        }
        
        UiStateModel(uiState, deviceResult.bleDevice)
      }
      false -> {
        UiStateModel(UiState.DISCONNECTED, deviceResult.bleDevice)
      }
    }
  }
  
  fun bleToOverlayState(deviceResult: DeviceResult.DeviceUpdateResult): OverlayStateModel {
    val peripheral = deviceResult.bleDevice.peripheral
    
    return when (deviceResult.bleDevice.isConnected() && peripheral != null) {
      true -> {
        
        val overlayState = when {
          peripheral.transmitter.batteryState == BatteryState.BATTERY_LOW -> OverlayState.BATTERY_LOW
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