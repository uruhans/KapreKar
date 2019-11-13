package com.coloplast.olympus.shared

import android.annotation.SuppressLint
import android.content.Context
import com.coloplast.olympus.shared.preferences.Preferences

@SuppressLint("StaticFieldLeak")
object AppSettings {
  
  fun initialize(applicationContext: Context, serverUrl: String) {
    SERVER_IP = serverUrl
    
    Preferences.initialize(applicationContext)
    AppSettings.applicationContext = applicationContext
  }
  
  private lateinit var SERVER_IP: String
  
  val ENVIRONMENT_URL by lazy {
    SERVER_IP
  }
  
  
  /**
   * usually you dont want to have a static reference to a context
   * (very common reason for leaks), but in this case, we will only
   * ever have a single instance of the application, and if the application
   * is destroyed this static field is destroyed too
   */
  lateinit var applicationContext: Context
}