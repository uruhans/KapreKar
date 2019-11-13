package com.coloplast.olympus.utils

import android.os.Build

object DeviceUtils {

  fun getDeviceModel() : String {
    return "${Build.MANUFACTURER} ${Build.MODEL}"
  }

}