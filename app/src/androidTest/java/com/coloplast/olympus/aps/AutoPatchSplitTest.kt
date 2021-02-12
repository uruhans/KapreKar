package com.coloplast.olympus.aps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coloplast.olympus.aps.models.StateChangeData
import com.coloplast.olympus.bluetooth.businesslayer.MockApsBluetoothManagerImpl
import com.coloplast.olympus.bluetooth.businesslayer.enums.Attached
import com.coloplast.olympus.bluetooth.model.PatchProbability
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.preferences.IPreferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.UpdatedStateChange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
class AutoPatchSplitTest : BaseKoinTest() {

  private lateinit var transmitterManager: TransmitterManager

  private val tx1 = "AA:BB:CC:DD:EE:FF"
  private val tx2 = "FF:EE:DD:CC:BB:AA"

  private val compromised_leak = 50
  private val compromised_new_patch = 10

  @Before
  fun init() {
    transmitterManager = get()
  }

  @Test
  fun test() {
    assertTrue(transmitterManager.bluetoothManager is MockApsBluetoothManagerImpl)

    // OK
    var statusChange = StateChangeData.ok(tx1, compromised = compromised_new_patch)
    determineActiveTxAndPatchCommand(statusChange)

    assertTrue(transmitterManager.isActiveTransmitter(tx1))

    // INNER BREACH
    statusChange = StateChangeData.innerbreach(tx1, compromised = compromised_leak)
    determineActiveTxAndPatchCommand(statusChange)

    // INNER BREACH - DETACH
    statusChange = StateChangeData.innerbreach(macAddress = tx1, attached = Attached.DETACHED, compromised = compromised_leak)
    determineActiveTxAndPatchCommand(statusChange)

    // NEW TRANSMITTER - OK
    statusChange = StateChangeData.ok(tx2, compromised = compromised_leak)
    val updatedStateChange = determineActiveTxAndPatchCommand(statusChange)
    assertTrue(transmitterManager.isActiveTransmitter(tx2))

    assertTrue(updatedStateChange.activeTransmitterChanged)
    assertEquals(PatchProbability.SAME, updatedStateChange.stateChange.patchProbability)
  }


  private fun detachTransmitters(compromised: Int) {
    var statusChange = StateChangeData.innerbreach(macAddress = tx1, attached = Attached.DETACHED, compromised = compromised)
    determineActiveTxAndPatchCommand(statusChange)
    statusChange = StateChangeData.innerbreach(macAddress = tx2, attached = Attached.DETACHED, compromised = compromised)
    determineActiveTxAndPatchCommand(statusChange)
  }

  private fun determineActiveTxAndPatchCommand(statusChange: StateChange) : UpdatedStateChange {
    val updatedStateChange = transmitterManager.determineCurrentlyActiveTransmitterAndReturnStateChangeFromIt(statusChange)
    transmitterManager.determinePatchCommand(updatedStateChange)
    return updatedStateChange
  }

}