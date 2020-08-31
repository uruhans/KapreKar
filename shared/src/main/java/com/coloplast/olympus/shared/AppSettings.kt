package com.coloplast.olympus.shared

import android.annotation.SuppressLint
import android.content.Context
import com.coloplast.olympus.shared.preferences.Preferences

@SuppressLint("StaticFieldLeak")
object AppSettings {
  
  fun initialize(applicationContext: Context, serverUrl: String) {
    SERVER_IP = serverUrl
    AppSettings.applicationContext = applicationContext
  }
  
  private lateinit var SERVER_IP: String
  private const val API_VERSION = "v1"
  
  val ENVIRONMENT_URL by lazy {
    "$SERVER_IP/api/$API_VERSION/"
  }

  val AZURE_ENVIRONMENT_URL by lazy {
    "https://cp-fc-dev.azurefd.net/api/"
  }

  val code by lazy {
    "1aiab34Rsqn2jSVcyXevoG9rvS0HjS9lqf5g8XsUV8BnzRVk3a9u1g=="
  }
  
  /**
   * usually you dont want to have a static reference to a context
   * (very common reason for leaks), but in this case, we will only
   * ever have a single instance of the application, and if the application
   * is destroyed this static field is destroyed too
   */
  lateinit var applicationContext: Context
}