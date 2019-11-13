package com.coloplast.olympus.ui.common.listeners

interface FlowListener {
  fun onNextClicked() { }
  fun onPreviousClicked()
  fun onCancelFlow()
}