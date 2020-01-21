package com.coloplast.olympus

import android.app.Application
import com.coloplast.olympus.koin.koinModule
import com.coloplast.olympus.shared.AppSettings
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class CustomApplication : Application() {
  
  override fun onCreate() {
    super.onCreate()
    
    AndroidThreeTen.init(this)
    AppSettings.initialize(this, BuildConfig.ServerUrl)
    
    Timber.plant(Timber.DebugTree())
    
    startKoin {
      androidLogger()
      androidContext(this@CustomApplication)
      modules(koinModule)
    }
  }
}