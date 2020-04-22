package com.coloplast.olympus.koin

import android.app.NotificationManager
import android.content.Context
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.Me
import com.coloplast.olympus.bluetooth.BluetoothServiceViewModel
import com.coloplast.olympus.bluetooth.DeviceConnectionManager
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManagerImpl
import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.businesslayer.components.TransmitterComponentImpl
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.notifications.NotificationCenter
import com.coloplast.olympus.notifications.NotificationScheduler
import com.coloplast.olympus.notifications.OlympusNotificationManager
import com.coloplast.olympus.ui.feature.home.HomeViewModel
import com.coloplast.olympus.ui.feature.transmitters.TransmittersViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module {
  single { Me() }
  single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
  single { NotificationCenter(androidContext(), get())}
  single { OlympusNotificationManager(get()) }
  single { NotificationScheduler() }
  
  // Components
  single<BluetoothManager> { BluetoothManagerImpl(androidContext(), BuildConfig.DEBUG) }
  single<TransmitterComponent> { TransmitterComponentImpl() }
  
  single { AddTransmitterViewModel(get(), get()) }
  single { BluetoothServiceViewModel(get(), get(), get()) }
  
  single { DeviceConnectionManager() }
  single { TransmittersViewModel(get(), get()) }
  
  factory { HomeViewModel(get()) }
  factory { DeviceConnection(get()) }
  
}