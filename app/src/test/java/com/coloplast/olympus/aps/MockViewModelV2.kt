package com.coloplast.olympus.aps

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.patchsplit.AutoPatchDetectionEngine
import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.patchsplit.models.CompromisedVault
import com.coloplast.olympus.patchsplit.models.CycleResult
import com.coloplast.olympus.shared.preferences.MockPreferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.UpdatedStateChange
import com.coloplast.olympus.ui.feature.home.models.StateTransformers
import com.coloplast.olympus.ui.feature.statemachine.OverlayStateMachineImpl
import com.coloplast.olympus.ui.feature.statemachine.UiStateMachineImpl
import io.reactivex.Observable
import org.junit.Assert
import org.mockito.Mockito

class MockViewModelV2(compromisedVault: CompromisedVault) {

  private val preferences = MockPreferences()
  private val engine = AutoPatchDetectionEngine(compromisedVault)

  private val bluetoothManager = Mockito.mock(BluetoothManager::class.java)
  private val transmitterManager = TransmitterManager(preferences, bluetoothManager)

  private val stateMachine = UiStateMachineImpl()
  private val overlayStateMachine = OverlayStateMachineImpl()

  init {
    Mockito.`when`(bluetoothManager.bleConnectionStateObservable(Mockito.anyString())).thenReturn(Observable.just(BleConnectionState.CONNECTED))
    Mockito.`when`(bluetoothManager.isConnected(Mockito.anyString())).thenReturn(true)

    Assert.assertTrue(bluetoothManager.isConnected(""))
    Assert.assertTrue(bluetoothManager.bleConnectionStateObservable("").blockingFirst() == BleConnectionState.CONNECTED)
    Assert.assertNotNull(transmitterManager)
  }

  fun run(bleDevice: BleDevice) : UpdatedStateChange {
    val deviceResult = DeviceResult.DeviceUpdateResult(bleDevice)

    val stateModel = StateTransformers.bleToUiState(deviceResult)
    val newUiState = stateMachine.invoke(stateModel.state)
    val overlayStateModel = StateTransformers.bleToOverlayState(deviceResult)
    val newOverlayState = overlayStateMachine.invoke(overlayStateModel.overlayState)

    return runService(StateChange(newUiState, newOverlayState, deviceResult.bleDevice))
  }

  private fun runService(stateChange: StateChange) : UpdatedStateChange {
    val updatedStateChange = transmitterManager.determineCurrentlyActiveTransmitterAndReturnStateChangeFromIt(stateChange)
    val lastValidDetach = transmitterManager.lastValidDetachState(updatedStateChange.stateChange.getMacAddress())
    val result = engine.determineAttachCycle(updatedStateChange, lastValidDetach != null)

    val action = if(result is CycleResult.Success) {
      engine.determineApsAction(result.attachCycleType)
    }
    else {
      ApsAction.Nothing()
    }

    updatedStateChange.action = action
    return updatedStateChange
  }

  fun isActiveTransmitter(macAddress: String) : Boolean {
    return transmitterManager.isActiveTransmitter(macAddress)
  }

}