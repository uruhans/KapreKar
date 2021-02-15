package com.coloplast.olympus.aps

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.MockApsBluetoothManagerImpl
import com.coloplast.olympus.shared.preferences.IPreferences
import com.coloplast.olympus.shared.preferences.MockPreferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import org.koin.dsl.module

val koinModule = module(override = true) {
  //single<IPreferences> { MockPreferences() }
  //single<BluetoothManager> { MockApsBluetoothManagerImpl() }
  //single { TransmitterManager(get(), get()) }
}