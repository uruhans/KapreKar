package com.coloplast.olympus.aps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coloplast.olympus.aps.models.DataBleDevice
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.bluetooth.model.PatchProbability
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.preferences.MockPreferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.UpdatedStateChange
import com.coloplast.olympus.ui.feature.home.models.StateTransformers
import com.coloplast.olympus.ui.feature.statemachine.OverlayStateMachineImpl
import com.coloplast.olympus.ui.feature.statemachine.UiStateMachineImpl
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class AutoPatchSplitTest {

  private lateinit var transmitterManager: TransmitterManager
  private val stateMachine = UiStateMachineImpl()
  private val overlayStateMachine = OverlayStateMachineImpl()

  private val tx1 = "AA:BB:CC:DD:EE:FF"
  private val tx2 = "FF:EE:DD:CC:BB:AA"

  private val compromised_leak = 50
  private val compromised_new_patch = 10

  private val preferences = MockPreferences()

  private val bluetoothManager = mock(BluetoothManager::class.java)

  @Before
  fun init() {
    `when`(bluetoothManager.bleConnectionStateObservable(anyString())).thenReturn(Observable.just(BleConnectionState.CONNECTED))
    `when`(bluetoothManager.isConnected(anyString())).thenReturn(true)

    transmitterManager = TransmitterManager(preferences, bluetoothManager)
  }

  @Test
  fun test_OkTx_InnerBreach_changeTx_Ok() {
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = byteArrayOf(/* patch */ 17, 0, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */1, /* patchType */0, /*compromised*/0, 0, 0, 0, 0))
    var updatedStateChange = runViewModel(okDevice)

    assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    assertTrue(transmitterManager.isActiveTransmitter(tx1))

    // INNER BREACH
    val innerbreachDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = byteArrayOf(/* patch */ 17, 1, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */1, /* patchType */0, /*compromised*/compromised_leak.toByte(), 0, 0, 0, 0))
    updatedStateChange = runViewModel(innerbreachDevice)

    assertTrue(updatedStateChange.stateChange.state == UiState.INNER_BREACH_B)

    // DETACH
    val innerbreachDetachDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = byteArrayOf(/* patch */ 17, 1, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */0, /* patchType */0, /*compromised*/compromised_leak.toByte(), 0, 0, 0, 0))
    updatedStateChange = runViewModel(innerbreachDetachDevice)

    assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)
    assertTrue(updatedStateChange.stateChange.bleDevice.isAttached().not())

    // NEW TRANSMITTER - OK
    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = byteArrayOf(/* patch */ 17, 0, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */1, /* patchType */0, /*compromised*/compromised_leak.toByte(), 0, 0, 0, 0))
    updatedStateChange = runViewModel(okDevice2)
    assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    assertTrue(transmitterManager.isActiveTransmitter(tx2))
    assertTrue(updatedStateChange.activeTransmitterChanged)
    assertEquals(PatchProbability.SAME, updatedStateChange.stateChange.patchProbability)
  }

  private fun runViewModel(bleDevice: BleDevice) : UpdatedStateChange {
    val deviceResult = DeviceResult.DeviceUpdateResult(bleDevice)

    val stateModel = StateTransformers.bleToUiState(deviceResult)
    val newUiState = stateMachine.invoke(stateModel.state)
    val overlayStateModel = StateTransformers.bleToOverlayState(deviceResult)
    val newOverlayState = overlayStateMachine.invoke(overlayStateModel.overlayState)

    return runService(StateChange(newUiState, newOverlayState, deviceResult.bleDevice))
  }

  private fun runService(stateChange: StateChange) : UpdatedStateChange {
    val updatedStateChange = transmitterManager.determineCurrentlyActiveTransmitterAndReturnStateChangeFromIt(stateChange)
    transmitterManager.determinePatchCommand(updatedStateChange)
    return updatedStateChange
  }

}