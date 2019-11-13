package com.coloplast.olympus.ui.common.extensions

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableArrayList

fun <T> ObservableArrayList<T>.notifyChanged() {
  val field = javaClass.getDeclaredField("mListeners")
  field.isAccessible = true
  
  val listeners = field.get(this)
  
  if (listeners != null && listeners is ListChangeRegistry) {
    listeners.notifyChanged(this)
  }
}