package com.coloplast.olympus.aps

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.patchsplit.AutoPatchDetectionEngineImpl
import com.coloplast.olympus.patchsplit.models.CompromisedVault
import com.coloplast.olympus.shared.preferences.MockPreferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.UpdatedStateChange
import com.coloplast.olympus.ui.feature.home.models.StateTransformers
import com.coloplast.olympus.ui.feature.statemachine.OverlayStateMachineImpl
import com.coloplast.olympus.ui.feature.statemachine.UiStateMachineImpl
import io.reactivex.Observable
import org.junit.Assert
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.random.Random

class MockViewModelV2(compromisedVault: CompromisedVault, randomizer: Random? = null) {

  private val preferences = MockPreferences()
  private val apsEngine = if(randomizer == null) AutoPatchDetectionEngineImpl(compromisedVault) else AutoPatchDetectionEngineImpl(compromisedVault, randomizer)

  private val bluetoothManager = mock(BluetoothManager::class.java)
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
    val cycleResult = apsEngine.determineAttachCycle(updatedStateChange, lastValidDetach != null)
    val apsMethod = apsEngine.determineApsMethod(cycleResult)
    val apsAction = apsEngine.determineApsAction(apsMethod)

    updatedStateChange.stateChange.apsAction = apsAction
    updatedStateChange.stateChange.resetOrStitchCandidate = apsEngine.resetOrStitchCandidate(apsAction, lastValidDetach)

    return updatedStateChange
  }

  fun isActiveTransmitter(macAddress: String) : Boolean {
    return transmitterManager.isActiveTransmitter(macAddress)
  }

}