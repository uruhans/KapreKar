package com.coloplast.olympus.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.coloplast.olympus.shared.AppSettings
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*


object OSUtils {
  fun isIntentHandled(intent: Intent): Boolean {
    val manager = AppSettings.applicationContext.packageManager
    val list = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return list.isNotEmpty()
  }

  fun firstDayOfWeek(): DayOfWeek {
    return WeekFields.of(Locale.getDefault())
      .firstDayOfWeek
  }

  fun bleLocationPermission(): String {
    val sdkInt = Build.VERSION.SDK_INT

    return if (sdkInt >= 29) {
      Manifest.permission.ACCESS_FINE_LOCATION
    }
    else {
      Manifest.permission.ACCESS_COARSE_LOCATION
    }
  }

  fun openAppSettings(context: Activity, channel: String? = null) {
    val intent = Intent().apply {
      when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
          if (channel != null) {
            action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_CHANNEL_ID, channel)
          }
          else {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
          }
        }
        else -> {
          action = "android.settings.APP_NOTIFICATION_SETTINGS"
          putExtra("app_package", context.packageName)
          putExtra("app_uid", context.applicationInfo.uid)
        }
      }
    }
    context.startActivity(intent)
  }

  fun isVersionOrAbove(androidVersion: Int) : Boolean {
    return Build.VERSION.SDK_INT >= androidVersion
  }
}