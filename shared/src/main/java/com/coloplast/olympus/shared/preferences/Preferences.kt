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
  
  private fun clearUserToken() {
    userToken = ""
  }
  
  fun clear() {
    clearUserToken()
  }
  
  companion object {
    private const val USER_TOKEN: String = "USER_TOKEN"
    
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