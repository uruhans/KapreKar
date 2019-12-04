package com.coloplast.olympus.koin

import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.Me
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.ui.feature.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val koinModule = module {
  
  // Components
  single { BluetoothManager(androidContext(), BuildConfig.DEBUG) }
  single { Me() }
  
  // ViewModels
  single { HomeViewModel(get()) }
  
}