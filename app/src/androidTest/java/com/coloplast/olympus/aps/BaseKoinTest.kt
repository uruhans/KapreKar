package com.coloplast.olympus.aps

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.MockApsBluetoothManagerImpl
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.shared.preferences.IPreferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest
import org.koin.test.get

open class BaseKoinTest : KoinTest {

  @Before
  fun setup() {
    loadKoinModules(koinModule)
  }

  @Test
  fun test_KoinReady() {
    val bluetoothManager = get<BluetoothManager>()
    val preferences = get<IPreferences>()
    val transmitterManager = get<TransmitterManager>()

    assertEquals(true, bluetoothManager is MockApsBluetoothManagerImpl)
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

  @After
  fun tearDown() {
    unloadKoinModules(koinModule)
  }
}