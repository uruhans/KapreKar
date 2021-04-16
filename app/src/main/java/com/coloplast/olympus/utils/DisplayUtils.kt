package com.coloplast.olympus.utils

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import com.coloplast.olympus.shared.AppSettings

class DisplayUtils {

  private var displayState = DisplayState.ON
  private var displayStateReceiver : DisplayStateReceiver? = null

  fun setDisplayState(displayState: DisplayState) {
    this.displayState = displayState
  }

  fun isDisplayOn() : Boolean {
    return displayState == DisplayState.ON
  }

  fun screenWidth(): Int {
    return AppSettings.applicationContext.resources.displayMetrics.widthPixels
  }

  fun registerDisplayStateReceiver(activity: Activity) {
    if(displayStateReceiver == null) {
      displayStateReceiver = DisplayStateReceiver()
    }

    IntentFilter().let {
      it.addAction(Intent.ACTION_SCREEN_ON)
      it.addAction(Intent.ACTION_SCREEN_OFF)
      activity.registerReceiver(displayStateReceiver, it)
    }
  }

  fun unregisterDisplayStateReceiver(activity: Activity) {
    displayStateReceiver?.let {
      activity.unregisterReceiver(it)
    }

    displayStateReceiver = null
  }

  enum class DisplayState {
    ON,
    OFF
  }
}