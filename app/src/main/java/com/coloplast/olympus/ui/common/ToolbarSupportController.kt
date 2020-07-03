package com.coloplast.olympus.ui.common

import androidx.databinding.ObservableBoolean

class ToolbarSupportController(val toolbarTitle: Any? = null, val listener: Listener? = null) {
  interface Listener {
    fun onBackClicked()
    fun onCloseClicked()
  }

  val showBackButton = ObservableBoolean(true)
  val showCloseButton = ObservableBoolean(true)
  
  fun onBackClicked() {
    listener?.onBackClicked()
  }
  
  fun onCloseClicked() {
    listener?.onCloseClicked()
  }
}