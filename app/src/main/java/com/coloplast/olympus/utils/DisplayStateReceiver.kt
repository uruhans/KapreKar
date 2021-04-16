package com.coloplast.olympus.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class DisplayStateReceiver : BroadcastReceiver(), KoinComponent {

  private val displayUtils: DisplayUtils by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    intent?.action?.let {
      when (it) {
        Intent.ACTION_SCREEN_ON -> {
          displayUtils.setDisplayState(DisplayUtils.DisplayState.ON)
        }
        Intent.ACTION_SCREEN_OFF -> {
          displayUtils.setDisplayState(DisplayUtils.DisplayState.OFF)
        }
      }
    }
  }
}