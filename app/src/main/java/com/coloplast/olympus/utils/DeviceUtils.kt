package com.coloplast.olympus.utils

import android.os.Build
import com.coloplast.olympus.shared.AppSettings

object DeviceUtils {
  
  fun getDeviceModel(): String {
    return "${Build.MANUFACTURER} ${Build.MODEL}"
  }
  
  fun screenWidth(): Int {
    return AppSettings.applicationContext.resources.displayMetrics.widthPixels
  }
  
}