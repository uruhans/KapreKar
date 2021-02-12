package com.coloplast.olympus.aps

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.MockApsBluetoothManagerImpl
import org.koin.dsl.module

val koinModule = module(override = true) {
  single<BluetoothManager> { MockApsBluetoothManagerImpl() }
}