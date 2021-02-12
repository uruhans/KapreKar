package com.coloplast.olympus.aps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
class AutoPatchSplitTest : BaseKoinTest() {

  @Test
  fun test_KoinReady() {
    val bluetoothManager = get<BluetoothManager>()
    val preferences = get<Preferences>()
    val transmitterManager = get<TransmitterManager>()

    assertNotNull(bluetoothManager)
    assertNotNull(preferences)
    assertNotNull(transmitterManager)
  }

  @Test
  fun test_VerifyMockedBluetoothManager() {
    val bluetoothManager = get<BluetoothManager>()

    assertEquals(true, bluetoothManager.isConnected(""))
    assertEquals(BleConnectionState.CONNECTED, bluetoothManager.bleConnectionStateObservable("").blockingFirst())
  }
}