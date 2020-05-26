package com.coloplast.olympus.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.R
import com.coloplast.olympus.shared.AppSettings

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

  fun screenWidth(): Int {
    return AppSettings.applicationContext.resources.displayMetrics.widthPixels
  }

  fun showSoftKeyboard(context: Context){
    val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
  }

  fun closeSoftKeyboard(view: View){
    /*val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.SHOW_IMPLICIT)*/

    val inputManager =
      view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
  }
}