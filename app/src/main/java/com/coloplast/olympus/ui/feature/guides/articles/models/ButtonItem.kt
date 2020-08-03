package com.coloplast.olympus.ui.feature.guides.articles.models

import androidx.annotation.StringRes
import com.coloplast.olympus.R
import com.coloplast.olympus.utils.UiUtils

class ButtonItem(@StringRes val title: Int, private val buttonType: ButtonType = ButtonType.BACK, private val listener: Listener) {
  
  interface Listener {
    fun onButtonClicked(buttonType: ButtonType)
  }
  
  val backgroundColor = UiUtils.getColor(if (buttonType == ButtonType.BACK) R.color.white else R.color.colorAccent)
  val textColor = UiUtils.getColor(if (buttonType == ButtonType.BACK) R.color.text_grey else R.color.text_white)
  
  fun onButtonClicked() {
    listener.onButtonClicked(buttonType)
  }
}