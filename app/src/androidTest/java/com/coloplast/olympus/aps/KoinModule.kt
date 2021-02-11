package com.coloplast.olympus.aps

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module(override = true) {
  single { Preferences(androidContext()) }
  single<BluetoothManager> { MockBluetoothManagerImpl() }
  single { TransmitterManager(get(), get()) }
}