package com.coloplast.olympus.shared.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.coloplast.olympus.shared.extensions.getLocalTime
import com.coloplast.olympus.shared.extensions.getSensors
import com.coloplast.olympus.shared.extensions.putLocalTime
import com.coloplast.olympus.shared.extensions.putSensors
import com.coloplast.olympus.shared.types.Sensors
import java.time.LocalTime
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class SharedPreferenceDelegate<T>(
  private val context: Context,
  private val defaultValue: T,
  private val getter: SharedPreferences.(String, T) -> T,
  private val setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
  private val key: String
) : ReadWriteProperty<Any, T> {

  private val sharedPreferences: SharedPreferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(context)
  }

  override fun getValue(thisRef: Any, property: KProperty<*>) =
    sharedPreferences.getter(key, defaultValue)

  override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
    sharedPreferences.edit().setter(key, value).commit()
  }
}

@Suppress("UNCHECKED_CAST")
fun <T> bindSharedPreference(
  context: Context, key: String, defaultValue: T
): ReadWriteProperty<Any, T> = when (defaultValue) {
  is Boolean -> SharedPreferenceDelegate(
    context, defaultValue, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean, key
  )
  is Int -> SharedPreferenceDelegate(
    context, defaultValue, SharedPreferences::getInt, SharedPreferences.Editor::putInt, key
  )
  is Long -> SharedPreferenceDelegate(
    context, defaultValue, SharedPreferences::getLong, SharedPreferences.Editor::putLong, key
  )
  is Float -> SharedPreferenceDelegate(
    context, defaultValue, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat, key
  )
  is String -> SharedPreferenceDelegate(
    context, defaultValue, SharedPreferences::getString, SharedPreferences.Editor::putString, key
  )
  is LocalTime -> SharedPreferenceDelegate(
    context, defaultValue, SharedPreferences::getLocalTime, SharedPreferences.Editor::putLocalTime, key
  )
  is Sensors -> SharedPreferenceDelegate(
    context, defaultValue, SharedPreferences::getSensors, SharedPreferences.Editor::putSensors, key
  )
  else -> throw IllegalArgumentException()
} as ReadWriteProperty<Any, T>