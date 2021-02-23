package com.coloplast.olympus.aps

import com.coloplast.olympus.aps.data.DataBleDevice
import com.coloplast.olympus.aps.data.MockVector
import com.coloplast.olympus.bluetooth.businesslayer.enums.Attached
import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.patchsplit.models.ApsMethod
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
  fun test_From100to20_wiggle_wiggle_resultDetachVectorExist_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    var detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    var charging = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.CHARGING_VECTOR())
    updatedStateChange = viewModel.run(charging)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_BATTERY_CHARGING_W)

    detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    charging = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.CHARGING_VECTOR())
    updatedStateChange = viewModel.run(charging)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_BATTERY_CHARGING_W)

    detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    charging = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.CHARGING_VECTOR())
    updatedStateChange = viewModel.run(charging)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_BATTERY_CHARGING_W)

    detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(100)

    val newDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(20, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test1_From100To0_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

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

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test2_From80To60_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

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

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test3_From0To100_resultStitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(100, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test4_From0To0_resultSplit() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(23)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test4_From0To0_resultReset() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(634)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test5_From40To40_resultStitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(10)
    compromisedVault.set(20)
    compromisedVault.set(30)
    compromisedVault.set(40)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(40, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test6_From20To60_resultStitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(10)
    compromisedVault.set(20)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test7_From20To60_resultReset() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(5, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test8_From20To0_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)
    compromisedVault.set(10)
    compromisedVault.set(20)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun test9_From100To0_newTx_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)
    compromisedVault.set(10)
    compromisedVault.set(20)
    compromisedVault.set(30)
    compromisedVault.set(40)
    compromisedVault.set(50)
    compromisedVault.set(100)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test10_From80To60_newTx_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)
    compromisedVault.set(10)
    compromisedVault.set(20)
    compromisedVault.set(30)
    compromisedVault.set(40)
    compromisedVault.set(50)
    compromisedVault.set(80)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test11_From0To100_newTx_resultStitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(100, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test12_From0To0_newTx_resultSplit() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(429)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test12_From0To0_newTx_resultReset() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(999)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test13_From40To40_newTx_resultStitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)


    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(10)
    compromisedVault.set(20)
    compromisedVault.set(30)
    compromisedVault.set(40)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(40, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test14_From20To60_newTx_resultStitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(10)
    compromisedVault.set(20)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test15_From5To5_newTx_resultReset() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(5, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun test16_From20To0_newTx_resultSplit() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5)
    compromisedVault.set(10)
    compromisedVault.set(20)

    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun testVar9_From100To0_result_C_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(100, LocalDateTime.now().minusHours(13))

    // detach occurs but we dont know about it
     val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)



    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.C)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testVar10_From80To60_result_C_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(80, LocalDateTime.now().minusHours(13))

    // detach occurs but we dont know about it
    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)



    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.C)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testVar11_From0To100_result_C_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0, LocalDateTime.now().minusHours(13))

    // detach occurs but we dont know about it
    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)



    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(100, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.C)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testVar12_From0To0_result_C_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(0, LocalDateTime.now().minusHours(13))

    // detach occurs but we dont know about it
    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)


    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.C)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testVar13_From40To40_result_C_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(40, LocalDateTime.now().minusHours(13))

    // detach occurs but we dont know about it
    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(40, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.C)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testVar14_From20To60_result_C_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(20, LocalDateTime.now().minusHours(13))

    // detach occurs but we dont know about it
    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.C)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testVar15_From5To5_result_C_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(5, LocalDateTime.now().minusHours(13))

    // detach occurs but we dont know about it
    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(5, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.C)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testVar16_From20To0_result_C_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)

    compromisedVault.set(20, LocalDateTime.now().minusHours(13))

    // detach occurs but we dont know about it
    val detach = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(detach)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.C)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_WID_From100To0_result_D_Nothing_Then_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.WID_VECTOR())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.WATER_INGRESS_DETECTION_J)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.apsMethod is ApsMethod.D)

    compromisedVault.set(100)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_CG_BROKEN_From80To60_result_D_Nothing_Then_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.BROKEN_VECTOR())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.CUTGUARD_BROKEN_I)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.apsMethod is ApsMethod.D)

    compromisedVault.set(80)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_CG_DIRTY_From0To100_result_D_Nothing_Then_Stitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.DIRTY_VECTOR())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.apsMethod is ApsMethod.D)

    compromisedVault.set(0)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(100, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_CG_NOISE_From0To0_result_D_Nothing_Then_Split() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(23)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.CUTGUARD_NOISE())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.apsMethod is ApsMethod.D)

    compromisedVault.set(0)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_CG_NOISE_From0To0_result_D_Nothing_Then_Reset() {
    val randomizer = mock(Random::class.java)
    Mockito.`when`(randomizer.nextInt(0, 1000)).thenReturn(600)

    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault, randomizer)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.CUTGUARD_NOISE())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.apsMethod is ApsMethod.D)

    compromisedVault.set(0)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_WID_From40To40_result_D_Nothing_Then_Stitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.WID_VECTOR())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.WATER_INGRESS_DETECTION_J)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.apsMethod is ApsMethod.D)

    compromisedVault.set(40)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(40, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_CG_BROKEN_From20To60_result_D_Nothing_Then_Stitch() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.BROKEN_VECTOR())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.CUTGUARD_BROKEN_I)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.apsMethod is ApsMethod.D)

    compromisedVault.set(20)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Stitch)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_CG_DIRTY_From5To5_result_D_Nothing_Then_Reset() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.DIRTY_VECTOR())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.apsMethod is ApsMethod.D)

    compromisedVault.set(5)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(5, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Reset)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_CG_NOISE_From20To0_result_D_Nothing_Then_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.CUTGUARD_NOISE())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("apsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.apsMethod is ApsMethod.D)

    compromisedVault.set(20)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("apsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }

  @Test
  fun testGroup3_MT_From40To40_then_take_A_off_result_D_Nothing_Then_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.B)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)

    compromisedVault.set(40)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice2)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.MULTIPLE_TRANSMITTERS)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.D)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.A)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun testGroup3_MT_From40To40_then_take_B_off_result_D_Nothing_Then_Split() {
    val compromisedVault = CompromisedVault()
    val viewModel = MockViewModelV2(compromisedVault)

    val detached = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    var updatedStateChange = viewModel.run(detached)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.TRANSMITTER_DETACHED_V)

    // TEST
    // OK
    val okDevice = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.B)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)

    compromisedVault.set(40)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.OK_VECTOR_NEW())
    updatedStateChange = viewModel.run(okDevice2)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.MULTIPLE_TRANSMITTERS)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.D)

    val newPatch = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.D)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Nothing)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx1))
  }


}