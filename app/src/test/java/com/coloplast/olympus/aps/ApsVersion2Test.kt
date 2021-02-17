package com.coloplast.olympus.aps

import com.coloplast.olympus.aps.data.DataBleDevice
import com.coloplast.olympus.aps.data.MockVector
import com.coloplast.olympus.bluetooth.businesslayer.enums.Attached
import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.patchsplit.models.CompromisedVault
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import org.junit.Assert
import org.junit.Test

class ApsVersion2Test {

  private val tx1 = "AA:BB:CC:DD:EE:FF"
  private val tx2 = "FF:EE:DD:CC:BB:AA"

  private val compromised_leak = 50
  private val compromised_new_patch = 10

  @Test
  fun test1_From100To0_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(10)
    compromisedVault.set(20)
    compromisedVault.set(30)
    compromisedVault.set(40)
    compromisedVault.set(50)
    compromisedVault.set(60)
    compromisedVault.set(70)
    compromisedVault.set(100)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.action is ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test2_From80To60_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    compromisedVault.set(10)
    compromisedVault.set(20)
    compromisedVault.set(30)
    compromisedVault.set(40)
    compromisedVault.set(50)
    compromisedVault.set(60)
    compromisedVault.set(70)
    compromisedVault.set(80)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.action is ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test3_From0To100_resultStitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(100, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.action is ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

}