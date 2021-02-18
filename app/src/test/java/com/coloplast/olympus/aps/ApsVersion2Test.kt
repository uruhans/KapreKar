package com.coloplast.olympus.aps

import com.coloplast.olympus.aps.data.DataBleDevice
import com.coloplast.olympus.aps.data.MockVector
import com.coloplast.olympus.bluetooth.businesslayer.enums.Attached
import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.patchsplit.models.CompromisedVault
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import kotlin.random.Random

class ApsVersion2Test {

  private val tx1 = "AA:BB:CC:DD:EE:FF"
  private val tx2 = "FF:EE:DD:CC:BB:AA"

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
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Split)
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
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Split)
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
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test4_From0To0_resultSplit() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(23)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test4_From0To0_resultReset() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(634)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test5_From40To40_resultStitch() {
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

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(40, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test6_From20To60_resultStitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(10)
    compromisedVault.set(20)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test7_From20To60_resultReset() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(5, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test8_From20To0_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)
    compromisedVault.set(10)
    compromisedVault.set(20)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test9_From100To0_newTx_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)
    compromisedVault.set(10)
    compromisedVault.set(20)
    compromisedVault.set(30)
    compromisedVault.set(40)
    compromisedVault.set(50)
    compromisedVault.set(100)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test10_From80To60_newTx_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)
    compromisedVault.set(10)
    compromisedVault.set(20)
    compromisedVault.set(30)
    compromisedVault.set(40)
    compromisedVault.set(50)
    compromisedVault.set(80)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test11_From0To100_newTx_resultStitch() {
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

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(100, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test12_From0To0_newTx_resultSplit() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(429)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test12_From0To0_newTx_resultReset() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(999)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test13_From40To40_newTx_resultStitch() {
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

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(40, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test14_From20To60_newTx_resultStitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(10)
    compromisedVault.set(20)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test15_From5To5_newTx_resultReset() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(5, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test16_From20To0_newTx_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)
    compromisedVault.set(10)
    compromisedVault.set(20)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun testVar1_From100To0_BLE_OOR_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    var updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)
    compromisedVault.set(10)
    compromisedVault.set(20)
    compromisedVault.set(50)
    compromisedVault.set(80)
    compromisedVault.set(100, LocalDateTime.now().minusHours(3))


    val newPatchOutOfRange = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_BODY_BLE))
    updatedStateChange = viewModel.run(newPatchOutOfRange)

    Assert.assertTrue(updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue(updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }


}