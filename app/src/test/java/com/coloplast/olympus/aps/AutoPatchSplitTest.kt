package com.coloplast.olympus.aps

import com.coloplast.olympus.aps.models.DataBleDevice
import com.coloplast.olympus.bluetooth.model.PatchProbability
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoPatchSplitTest {

  private val tx1 = "AA:BB:CC:DD:EE:FF"
  private val tx2 = "FF:EE:DD:CC:BB:AA"

  private val compromised_leak = 50
  private val compromised_new_patch = 10

  @Test
  fun test_OkTx_InnerBreach_changeTx_Ok() {
    val viewModel = MockViewModel()

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = byteArrayOf(/* patch */ 17, 0, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */1, /* patchType */0, /*compromised*/0, 0, 0, 0, 0))
    var updatedStateChange = viewModel.run(okDevice)

    assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    assertTrue(viewModel.isActiveTransmitter(tx1))

    // INNER BREACH
    val innerbreachDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = byteArrayOf(/* patch */ 17, 1, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */1, /* patchType */0, /*compromised*/compromised_leak.toByte(), 0, 0, 0, 0))
    updatedStateChange = viewModel.run(innerbreachDevice)

    assertTrue(updatedStateChange.stateChange.state == UiState.INNER_BREACH_B)

    // DETACH
    val innerbreachDetachDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = byteArrayOf(/* patch */ 17, 1, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */0, /* patchType */0, /*compromised*/compromised_leak.toByte(), 0, 0, 0, 0))
    updatedStateChange = viewModel.run(innerbreachDetachDevice)

    assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)
    assertTrue(updatedStateChange.stateChange.bleDevice.isAttached().not())

    // NEW TRANSMITTER - OK
    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = byteArrayOf(/* patch */ 17, 0, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */1, /* patchType */0, /*compromised*/compromised_leak.toByte(), 0, 0, 0, 0))
    updatedStateChange = viewModel.run(okDevice2)
    assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    assertTrue(viewModel.isActiveTransmitter(tx2))
    assertTrue(updatedStateChange.activeTransmitterChanged)
    assertEquals(PatchProbability.SAME, updatedStateChange.stateChange.patchProbability)
  }
}