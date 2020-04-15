package com.coloplast.olympus

import android.app.Application
import com.amitshekhar.DebugDB
import com.coloplast.olympus.koin.koinModule
import com.coloplast.olympus.shared.AppSettings
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class CustomApplication : Application() {
  
  override fun onCreate() {
    super.onCreate()
    
    AppSettings.initialize(this, BuildConfig.ServerUrl)
    
    Timber.plant(Timber.DebugTree())
    Timber.d(DebugDB.getAddressLog())
    
    startKoin {
      androidLogger()
      androidContext(this@CustomApplication)
      modules(koinModule)
    }
  }
}