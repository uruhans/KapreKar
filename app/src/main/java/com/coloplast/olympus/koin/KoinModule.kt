package com.coloplast.olympus.koin

import android.app.NotificationManager
import android.content.Context
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.Me
import com.coloplast.olympus.bluetooth.BluetoothServiceViewModel
import com.coloplast.olympus.bluetooth.businesslayer.*
import com.coloplast.olympus.businesslayer.components.TransmitterComponentImpl
import com.coloplast.olympus.businesslayer.components.UserComponentImpl
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.notifications.NotificationCenter
import com.coloplast.olympus.notifications.NotificationScheduler
import com.coloplast.olympus.notifications.OlympusNotificationManager
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.home.BreachesOccurred
import com.coloplast.olympus.ui.feature.home.HomeViewModel
import com.coloplast.olympus.ui.feature.onboarding.login.LoginViewModel
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.ForgotPasswordFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.ForgotPasswordViewModel
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideApplyingFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideConnectionFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideIntroFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuidePreparingFactoryImpl
import com.coloplast.olympus.ui.feature.onboarding.setup.applying.AwaitingConnectionViewModel
import com.coloplast.olympus.ui.feature.onboarding.setup.connection.SetupGuideLocationSGC4ViewModel
import com.coloplast.olympus.ui.feature.onboarding.signup.SignupViewModel
import com.coloplast.olympus.ui.feature.onboarding.valueproposition.ValuePropositionFactoryImpl
import com.coloplast.olympus.ui.feature.profile.ProfileViewModel
import com.coloplast.olympus.ui.feature.splash.SplashViewModel
import com.coloplast.olympus.ui.feature.transmitters.TransmittersViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterFactoryImpl
import com.coloplast.olympus.ui.feature.transmitters.add.at7.AddTransmitterAT7ViewModel
import com.coloplast.olympus.ui.feature.transmitters.add.at9.AddTransmitterAT9ViewModel
import com.coloplast.olympus.work.SyncViewModel
import com.coloplast.olympus.work.SyncWorker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module {
  /* Preferences */
  single { Preferences(androidContext()) }
  single { Me(get()) }
  factory { SyncWorker(get(), get()) }

  /* Notifications */
  single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
  single { NotificationCenter(androidContext(), get(), get()) }
  single { OlympusNotificationManager(get()) }
  single { NotificationScheduler() }

  /* Bluetooth */
  single<BluetoothManager> { BluetoothManagerImpl(get(), androidContext(), BuildConfig.DEBUG) }
  factory { DeviceConnection(get(), get()) }
  single<FirmwareManager> { FirmwareManagerImpl(androidContext(), get()) }

  single { TransmitterManager(get(), get()) }
  single { BreachesOccurred(get()) }

  /* Components */
  single<TransmitterComponent> { TransmitterComponentImpl(get()) }
  single<UserComponent> { UserComponentImpl(get()) }

  /* ViewModels */
  factory { BluetoothServiceViewModel(get(), get(), get()) }
  single { SplashViewModel(get(), get()) }
  single { HomeViewModel(get()) }
  single { TransmittersViewModel(get(), get()) }
  single { AddTransmitterAT9ViewModel(get(), get(), get()) }
  single { AwaitingConnectionViewModel(get()) }
  single { LoginViewModel(get(), get()) }
  single { ForgotPasswordViewModel(get()) }
  single { SetupGuideLocationSGC4ViewModel(get()) }
  single { AddTransmitterAT7ViewModel(get()) }
  single { SyncViewModel(get(), get(), get()) }
  single { ProfileViewModel(get(), get(), get()) }
  single { SignupViewModel(get(), get()) }

  /* Flows */
  single { AddTransmitterFactoryImpl() }
  single { ValuePropositionFactoryImpl() }
  single { SetupGuideIntroFactoryImpl() }
  single { SetupGuideConnectionFactoryImpl() }
  single { SetupGuidePreparingFactoryImpl() }
  single { SetupGuideApplyingFactoryImpl() }
  single { ForgotPasswordFactoryImpl() }
}