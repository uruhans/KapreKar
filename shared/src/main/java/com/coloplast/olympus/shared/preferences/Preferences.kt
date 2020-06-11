package com.coloplast.olympus.shared.preferences

import android.annotation.SuppressLint
import android.content.Context

/**
 * Normally you do not want to store the context in *any* class.
 * However, in this case, we store it as this class needs to be alive only when the application is alive,
 * and will be killed off once the application dies
 */
class Preferences(applicationContext: Context) {
  
  var userToken: String by bindSharedPreference(applicationContext, USER_TOKEN, "")
  var activeTransmitter: String by bindSharedPreference(applicationContext, ACTIVE_TRANSMITTER, "")
  var latestStateFromActiveTx: String by bindSharedPreference(applicationContext, LATEST_STATE_FROM_ACTIVE_TRANSMITTER, "")
  var leaks: String by bindSharedPreference(applicationContext, LEAKS, "")
  var areRingingAlertsEnabled: Boolean by bindSharedPreference(applicationContext, RINGING_ALERTS_ENABLED, true)

  private fun clearUserToken() {
    userToken = ""
  }
  
  fun clear() {
    clearUserToken()
  }
  
  companion object {
    private const val USER_TOKEN: String = "USER_TOKEN"
    private const val ACTIVE_TRANSMITTER: String = "ACTIVE_TRANSMITTER"
    private const val LATEST_STATE_FROM_ACTIVE_TRANSMITTER: String = "LATEST_STATE_FROM_ACTIVE_TRANSMITTER"
    private const val LEAKS: String = "LEAKS"
    private const val RINGING_ALERTS_ENABLED: String = "RINGING_ALERTS_ENABLED"

    @SuppressLint("StaticFieldLeak")
    private lateinit var preferences: Preferences
    
    fun initialize(applicationContext: Context) {
      preferences = Preferences(applicationContext)
    }
    
    fun get(): Preferences {
      return preferences
    }
  }
}