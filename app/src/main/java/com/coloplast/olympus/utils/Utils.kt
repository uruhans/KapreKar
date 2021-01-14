package com.coloplast.olympus.utils

import android.content.Context
import android.location.LocationManager
import com.coloplast.olympus.shared.AppSettings


object Utils {

  private const val UK_PREFIX_1 = "+44"
  private const val UK_PREFIX_2 = "0044"

  fun isUKNumber(phoneNumber: String?): Boolean {
    if(phoneNumber == null) {
      return false
    }

    return phoneNumber.contains(UK_PREFIX_1) || phoneNumber.contains(UK_PREFIX_2)
  }

  fun locationEnabled() : Boolean {
    val lm = AppSettings.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    return try {
      lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    catch (ex: Exception) {
      false
    }
  }

}