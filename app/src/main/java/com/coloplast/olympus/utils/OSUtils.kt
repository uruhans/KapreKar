package com.coloplast.olympus.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.coloplast.olympus.shared.AppSettings
import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
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
}