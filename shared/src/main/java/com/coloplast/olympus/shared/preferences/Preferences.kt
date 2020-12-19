package com.coloplast.olympus.shared.preferences

import android.content.Context
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.shared.types.Sensors
import java.time.LocalTime
import java.util.*

/**
 * Normally you do not want to store the context in *any* class.
 * However, in this case, we store it as this class needs to be alive only when the application is alive,
 * and will be killed off once the application dies
 */
class Preferences(applicationContext: Context) {

  var userLoggedIn: String by bindSharedPreference(applicationContext, USER_LOGGED_IN, "")
  private var userToken: String by bindSharedPreference(applicationContext, USER_TOKEN, "")
  var activeTransmitter: String by bindSharedPreference(applicationContext, ACTIVE_TRANSMITTER, "")
  var latestStateFromActiveTx: String by bindSharedPreference(applicationContext, LATEST_STATE_FROM_ACTIVE_TRANSMITTER, "")
  var leaks: String by bindSharedPreference(applicationContext, LEAKS, "")
  var areRingingAlertsEnabled: Boolean by bindSharedPreference(applicationContext, RINGING_ALERTS_ENABLED, true)
  var ringingAlertSensor: Sensors by bindSharedPreference(applicationContext, RINGING_ALERT_SENSOR, Sensors.ALL)
  var bedtimeStart: LocalTime by bindSharedPreference(applicationContext, BEDTIME_ALERT_START, LocalTime.of(22,0))
  var bedtimeEnd: LocalTime by bindSharedPreference(applicationContext, BEDTIME_ALERT_END, LocalTime.of(8,0))
  var bedtimeAlertSensor: Sensors by bindSharedPreference(applicationContext, RINGING_ALERT_SENSOR, Sensors.ALL)
  var didSeeWelcome: Boolean by bindSharedPreference(applicationContext, DID_SEE_WELCOME, false)
  var didSeeAppTourGuide: Boolean by bindSharedPreference(applicationContext, DID_SEE_APP_TOUR_GUIDE, false)
  var didSeeValueProposition: Boolean by bindSharedPreference(applicationContext, DID_SEE_VALUE_PROPOSITION, false)
  var onboardingCompletedTime: Long by bindSharedPreference(applicationContext, ONBOARDING_COMPLETED_TIMESTAMP, 0)
  private var currentOnboardingProgression : Int by bindSharedPreference(applicationContext, CURRENT_ONBOARDING_PROGRESSION, OnboardingProgression.NOT_STARTED.id)

  private var patchId: Int by bindSharedPreference(applicationContext, CURRENT_PATCH_ID, 0)
  private var deviceId: String by bindSharedPreference(applicationContext, CURRENT_DEVICE_ID, "")

  init {
    if(deviceId.isEmpty()) {
      deviceId = UUID.randomUUID().toString()
    }

    /**
     * There is no way to get the userToken (used in the DataLayer)
     * through this object - this is why its passed to the appsettings (not pretty no)
     */
    if(userToken.isNotBlank()) {
      AppSettings.userToken = userToken
    }
  }

  private fun clearUserToken() {
    userToken = ""
    userLoggedIn = ""
    AppSettings.userToken = ""
  }

  fun setCurrentOnboardingProgression(currentStep: OnboardingProgression) {
    currentOnboardingProgression = currentStep.id
  }

  fun getCurrentOnboardingProgress(): OnboardingProgression {
    return OnboardingProgression.fromId(currentOnboardingProgression)
  }

  fun incrementPatchId() {
    patchId++
  }

  fun getCurrentPatchId() : Int {
    return patchId
  }

  fun getUniqueDeviceId() : UUID {
    return UUID.fromString(deviceId)
  }

  fun clear() {
    clearUserToken()
  }

  fun setToken(token: String) {
    userToken = token
    AppSettings.userToken = token
  }

  fun getToken() : String {
    return userToken
  }
  
  companion object {
    private const val USER_LOGGED_IN: String = "USER_LOGGED_IN"
    private const val USER_TOKEN: String = "USER_TOKEN"
    private const val ACTIVE_TRANSMITTER: String = "ACTIVE_TRANSMITTER"
    private const val LATEST_STATE_FROM_ACTIVE_TRANSMITTER: String = "LATEST_STATE_FROM_ACTIVE_TRANSMITTER"
    private const val LEAKS: String = "LEAKS"
    private const val RINGING_ALERTS_ENABLED: String = "RINGING_ALERTS_ENABLED"
    private const val RINGING_ALERT_SENSOR: String = "RINGING_ALERT_SENSOR"
    private const val BEDTIME_ALERT_START: String = "BEDTIME_ALERT_START"
    private const val BEDTIME_ALERT_END: String = "BEDTIME_ALERT_END"
    private const val DID_SEE_VALUE_PROPOSITION: String = "DID_SEE_VALUE_PROPOSITION"
    private const val CURRENT_ONBOARDING_PROGRESSION: String = "CURRENT_ONBOARDING_PROGRESSION"
    private const val ONBOARDING_COMPLETED_TIMESTAMP: String = "ONBOARDING_COMPLETED_TIMESTAMP"
    private const val DID_SEE_APP_TOUR_GUIDE: String = "DID_SEE_APP_TOUR_GUIDE"
    private const val DID_SEE_WELCOME: String = "DID_SEE_WELCOME"
    private const val CURRENT_PATCH_ID: String = "CURRENT_PATCH_ID"
    private const val CURRENT_DEVICE_ID: String = "CURRENT_DEVICE_ID"

//    @SuppressLint("StaticFieldLeak")
//    private lateinit var preferences: Preferences

//    fun initialize(applicationContext: Context) {
//      preferences = Preferences(applicationContext)
//    }
//
//    fun get(): Preferences {
//      return preferences
//    }
  }
}