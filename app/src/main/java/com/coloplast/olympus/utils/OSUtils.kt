package com.coloplast.olympus.utils

import android.content.Intent
import android.content.pm.PackageManager
import com.coloplast.olympus.shared.AppSettings
import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
import timber.log.Timber
import java.io.InputStream
import java.util.*

object OSUtils {
  fun isIntentHandled(intent: Intent): Boolean {
    val manager = AppSettings.applicationContext.packageManager
    val list = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return list.isNotEmpty()
  }
  
  fun getFromAssets(filename: String): String {
    Timber.d("getFromAssets: $filename")
    return try {
      val inputStream: InputStream = AppSettings.applicationContext.resources.assets.open(filename)
      inputStream.bufferedReader()
        .use { it.readText() }
    }
    catch (e: Exception) {
      Timber.e("getFromAssets: $e")
      ""
    }
  }
  
  fun firstDayOfWeek(): DayOfWeek {
    return WeekFields.of(Locale.getDefault())
      .firstDayOfWeek
  }
}