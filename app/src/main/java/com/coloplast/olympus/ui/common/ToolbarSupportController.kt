package com.coloplast.olympus.ui.common

import androidx.databinding.ObservableBoolean

class ToolbarSupportController(
  val toolbarTitle: Any? = null,
  val listener: Listener? = null,
  val backButtonVisibility: Boolean = true,
  val closeButtonVisibility: Boolean = true
) {
  interface Listener {
    fun onBackClicked()
    fun onCloseClicked()
  }

  val showBackButton = ObservableBoolean(backButtonVisibility)
  val showCloseButton = ObservableBoolean(closeButtonVisibility)
  
  fun onBackClicked() {
    listener?.onBackClicked()
  }
  
  fun onCloseClicked() {
    listener?.onCloseClicked()
  }
}