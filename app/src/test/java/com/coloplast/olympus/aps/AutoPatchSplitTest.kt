package com.coloplast.olympus.aps

import com.coloplast.olympus.aps.data.DataBleDevice
import com.coloplast.olympus.aps.data.MockVector
import com.coloplast.olympus.aps.data.MockVector.toByteArray
import com.coloplast.olympus.bluetooth.businesslayer.models.StatusVector
import com.coloplast.olympus.bluetooth.model.PatchProbability
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mock

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
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    assertTrue(updatedStateChange?.stateChange?.state == UiState.OK_A)
    assertTrue(viewModel.isActiveTransmitter(tx1))

    // INNER BREACH
    // byteArrayOf(/* patch */ 17, 1, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */1, /* patchType */0, /*compromised*/compromised_leak.toByte(), 0, 0, 0, 0)
    val innerbreachDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.ONE_INNER_LEAK_VECTOR())
    updatedStateChange = viewModel.run(innerbreachDevice)

    assertTrue(updatedStateChange?.stateChange?.state == UiState.INNER_BREACH_B)

    // DETACH
    //  byteArrayOf(/* patch */ 17, 1, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */0, /* patchType */0, /*compromised*/compromised_leak.toByte(), 0, 0, 0, 0)
    val innerbreachDetachDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.ONE_INNER_LEAK_DETACHED_VECTOR())
    updatedStateChange = viewModel.run(innerbreachDetachDevice)

    assertTrue(updatedStateChange?.stateChange?.state == UiState.TRANSMITTER_DETACHED_V)
    assertTrue(updatedStateChange?.stateChange?.bleDevice?.isAttached()?.not() ?: false)

    // NEW TRANSMITTER - OLD PATCH
    // byteArrayOf(/* patch */ 17, 0, 0, 0, 0, 0, /* transmitter */ /* WID */0, /* attached */1, /* patchType */0, /*compromised*/compromised_leak.toByte(), 0, 0, 0, 0)
    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.OK_VECTOR_OLD())
    updatedStateChange = viewModel.run(okDevice2)
    assertTrue(updatedStateChange?.stateChange?.state == UiState.OK_A)

    assertTrue(viewModel.isActiveTransmitter(tx2))
    assertTrue(updatedStateChange?.activeTransmitterChanged ?: true)
    //assertEquals(PatchProbability.SAME, updatedStateChange?.stateChange?.patchProbability)
  }
}