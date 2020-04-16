package com.coloplast.olympus.ui.common

class ToolbarSupportController(val toolbarTitle: Any? = null, val listener: Listener? = null) {
  interface Listener {
    fun onBackClicked()
    fun onCloseClicked()
  }
  
  fun onBackClicked() {
    listener?.onBackClicked()
  }
  
  fun onCloseClicked() {
    listener?.onCloseClicked()
  }
}