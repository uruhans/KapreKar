package com.coloplast.olympus.koin

import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.Me
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManagerImpl
import com.coloplast.olympus.ui.feature.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module {
  single { Me() }
  
  // Components
  single<BluetoothManager> { BluetoothManagerImpl(androidContext(), BuildConfig.DEBUG) }
  
  // ViewModels
  single { HomeViewModel(get()) }
  
}