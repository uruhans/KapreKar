package com.coloplast.olympus.shared.extensions

import android.content.SharedPreferences
import java.time.LocalTime

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 18/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */

fun SharedPreferences.getLocalTime(key: String, defaultValue: LocalTime): LocalTime{
   return getString(key, null)?.run {
     val data = split(":")
     val hour = data[0].toInt()
     val minutes = data[1].toInt()
     return@run LocalTime.of(hour, minutes)
   } ?: defaultValue
}

fun SharedPreferences.Editor.putLocalTime(key: String, value: LocalTime) = putString(key, value.toString())
