package com.coloplast.olympus

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module {
  /* Preferences */
  single { Preferences(androidContext()) }
  single { TransmitterManager(get(), get()) }
  single { }


}