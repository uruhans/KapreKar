package com.coloplast.olympus.shared

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppSettings {
  
  fun initialize(applicationContext: Context, serverUrl: String, azureUrl: String, azureCode: String) {
    SERVER_IP = serverUrl
    AZURE_URL = azureUrl
    AZURE_CODE = azureCode
    AppSettings.applicationContext = applicationContext
  }
  
  private lateinit var SERVER_IP: String
  private lateinit var AZURE_URL: String
  private lateinit var AZURE_CODE: String
  private const val API_VERSION = "v1"
  
  val ENVIRONMENT_URL by lazy {
    "$SERVER_IP/api/$API_VERSION/"
  }

  val AZURE_ENVIRONMENT_URL by lazy {
    AZURE_URL
  }

  val code by lazy {
    AZURE_CODE
  }
  
  /**
   * usually you dont want to have a static reference to a context
   * (very common reason for leaks), but in this case, we will only
   * ever have a single instance of the application, and if the application
   * is destroyed this static field is destroyed too
   */
  lateinit var applicationContext: Context
}