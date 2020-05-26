package com.coloplast.olympus.ui.common.listeners

interface FlowListener {
  fun onNextClicked(result: Any? = null) { }
  fun onPreviousClicked()
  fun onCancelFlow()
}