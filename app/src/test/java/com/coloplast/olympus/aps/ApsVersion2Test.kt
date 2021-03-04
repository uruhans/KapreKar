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
  fun grp1_1_1() {
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
  fun grp1_1_2() {
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
  fun grp1_1_3() {
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
  fun grp1_1_4_split() {
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
  fun grp1_1_4_reset() {
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
  fun grp1_1_5() {
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
  fun grp1_1_6() {
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
  fun grp1_1_7() {
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
  fun grp1_1_8() {
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
  fun grp1_2_1() {
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
  fun grp1_2_2() {
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
  fun grp1_2_3() {
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
  fun grp1_2_4_split() {
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
  fun grp1_2_4_reset() {
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
  fun grp1_2_5() {
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
  fun grp1_2_6() {
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
  fun grp1_2_7() {
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
  fun grp1_2_8() {
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
  fun grp2_1() {
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
  fun grp2_2() {
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
  fun grp2_3() {
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

  /**
   * Not necessary to test the random in this test, as the +12 hour timeout happens
   */
  @Test
  fun grp2_4() {
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
  fun grp2_5() {
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
  fun grp2_6() {
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
  fun grp2_7() {
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
  fun grp2_8() {
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
  fun grp3_1_2() {
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
  fun grp3_2_2() {
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
  fun grp3_4_2_split() {
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
  fun grp3_4_2_reset() {
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
  fun grp3_5_2() {
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
  fun grp3_6_2() {
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
  fun grp3_7_2() {
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
  fun grp3_8_2() {
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
  fun grp3_9_2() {
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

    compromisedVault.set(20)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(60, Attached.ATTACHED_ON_PATCH))
    updatedStateChange = viewModel.run(okDevice2)
    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.MULTIPLE_TRANSMITTERS)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.D)

    val newPatch = DataBleDevice.bleDevice(tx1, statusVectorBytes = MockVector.OK_VECTOR_DETACHED())
    updatedStateChange = viewModel.run(newPatch)

    Assert.assertTrue("UiState was [${updatedStateChange.stateChange.state}]", updatedStateChange.stateChange.state == UiState.OK_A)
    Assert.assertTrue("ApsMethod was [${updatedStateChange.apsMethod}]", updatedStateChange.apsMethod is ApsMethod.C)
    Assert.assertTrue("ApsAction was [${updatedStateChange.stateChange.apsAction}]", updatedStateChange.stateChange.apsAction == ApsAction.Split)
    Assert.assertTrue(viewModel.isActiveTransmitter(tx2))
  }

  @Test
  fun grp3_9_3() {
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

    compromisedVault.set(60)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(40, Attached.ATTACHED_ON_PATCH))
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

  @Test
  fun grp3_9_4() {
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

    compromisedVault.set(0)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(0, Attached.ATTACHED_ON_PATCH))
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

  @Test
  fun grp3_9_5() {
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

    compromisedVault.set(5)

    val okDevice2 = DataBleDevice.bleDevice(tx2, statusVectorBytes = MockVector.dynamicVector(5, Attached.ATTACHED_ON_PATCH))
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

  @Test
  fun grp3_9_6() {
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