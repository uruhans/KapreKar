package com.coloplast.olympus.ui.feature.debug.model

class LogFile(val name: String, val path: String, val listener: Listener) {
  interface Listener {
    fun onItemClicked(path: String)
    fun onEmailClicked(path: String)
  }

  fun onLogClicked() {
    listener.onItemClicked(path)
  }

  fun onEmailClicked() {
    listener.onEmailClicked(path)
  }
}