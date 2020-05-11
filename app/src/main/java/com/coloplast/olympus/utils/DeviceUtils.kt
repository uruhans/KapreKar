package com.coloplast.olympus.utils

import android.content.Context
import android.os.Build
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.coloplast.olympus.shared.AppSettings


object DeviceUtils {
  
  fun getDeviceModel(): String {
    return "${Build.MANUFACTURER} ${Build.MODEL}"
  }
  
  fun screenWidth(): Int {
    return AppSettings.applicationContext.resources.displayMetrics.widthPixels
  }

  fun showSoftKeyboard(context: Context){
    val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
  }

  fun closeSoftKeyboard(context: Context){
    val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.SHOW_FORCED)
  }
}