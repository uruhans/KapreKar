package com.coloplast.olympus.koin

import android.app.NotificationManager
import android.content.Context
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.Me
import com.coloplast.olympus.bluetooth.BluetoothServiceViewModel
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManagerImpl
import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.businesslayer.components.TransmitterComponentImpl
import com.coloplast.olympus.businesslayer.components.UserComponentImpl
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.notifications.NotificationCenter
import com.coloplast.olympus.notifications.NotificationScheduler
import com.coloplast.olympus.notifications.OlympusNotificationManager
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.guides.flows.location.LocationGuideFactoryImpl
import com.coloplast.olympus.ui.feature.home.BreachesOccurred
import com.coloplast.olympus.ui.feature.home.HomeViewModel
import com.coloplast.olympus.ui.feature.onboarding.createprofile.CreateUserFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.createprofile.CreateUserViewModel
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.CreateUserFactoryModel
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.DataConsentFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.DataConsentModel
import com.coloplast.olympus.ui.feature.onboarding.login.LoginViewModel
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.ForgotPasswordFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.ForgotPasswordModel
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.ForgotPasswordViewModel
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideIntroFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuidePreparingFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.valueproposition.ValuePropositionFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.valueproposition.ValuePropositionModel
import com.coloplast.olympus.ui.feature.transmitters.TransmittersViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterFactoryImpl
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.at9.AddTransmitterAT9ViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module {

  /* Preferences */
  single { Preferences(androidContext()) }

  /* Notifications */
  single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
  single { NotificationCenter(androidContext(), get(), get())}
  single { OlympusNotificationManager(get()) }
  single { NotificationScheduler() }

  /* Bluetooth */
  single<BluetoothManager> { BluetoothManagerImpl(androidContext(), BuildConfig.DEBUG) }
  factory { DeviceConnection(get()) }

  single { TransmitterManager(get()) }
  single { BreachesOccurred() }

  /* Components */
  single<TransmitterComponent> { TransmitterComponentImpl() }
  single<UserComponent> { UserComponentImpl() }

  single { Me() }

  /* ViewModels */
  factory { BluetoothServiceViewModel(get(), get(), get()) }
  factory { HomeViewModel(get()) }
  single { AddTransmitterViewModel(get(), get()) }
  single { TransmittersViewModel(get(), get()) }
  single { AddTransmitterAT9ViewModel(get(), get()) }
  single { CreateUserViewModel(get(), get()) }
  single { LoginViewModel(get(), get()) }
  single { ForgotPasswordViewModel(get()) }

  /* Flows
  *  Fixed problem with Generic Type:
  *  https://stackoverflow.com/a/59641397/2145504
  *  */
  single<FlowFactory<AddTransmitterModel>>(override = true) { AddTransmitterFactoryImpl() }
  single<FlowFactory<ValuePropositionModel>>(override = true) { ValuePropositionFactoryImpl() }
  single { SetupGuideIntroFactoryImpl() }
  single { SetupGuidePreparingFactoryImpl() }
  single<FlowFactory<DataConsentModel>>(override = true) { DataConsentFactoryImpl() }
  single<FlowFactory<CreateUserFactoryModel>>(override = true) { CreateUserFactoryImpl() }
  single<FlowFactory<ForgotPasswordModel>>(override = true) { ForgotPasswordFactoryImpl() }
  single { LocationGuideFactoryImpl() }
}