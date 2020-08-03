package com.coloplast.olympus.ui.feature.guides.articles.models

class TopMenuItem(private val listener: Listener) {
  
  interface Listener {
    fun onBackClicked()
    fun onCloseClicked()
  }
  
  fun onBackClicked() {
    listener.onBackClicked()
  }
  
  fun onCloseClicked() {
    listener.onCloseClicked()
  }
}