package com.coloplast.olympus.utils

import android.os.Build
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.R
import com.coloplast.olympus.shared.AppSettings
import java.io.File

object DeviceUtils {

  fun getDeviceModel(): String {
    return "${Build.MANUFACTURER} ${Build.MODEL}"
  }

  @JvmStatic
  fun getAppVersion(): String {
    val appName = AppSettings.applicationContext.getString(R.string.launch_screen_title)

    BuildConfig.Suffix.let {
      return "$appName v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})$it"
    }
  }

  fun getVersionNameAndCode(): String {
    return "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
  }

  fun versionName(): String {
    return BuildConfig.VERSION_NAME
  }

  fun logDirectory() : File {
    return File(AppSettings.applicationContext.filesDir.absolutePath, "log")
  }

}