package com.coloplast.olympus.ui.feature.guides.models

import androidx.annotation.StringRes

class ButtonItem(
  @StringRes val text: Int,
  val buttonType: ButtonType = ButtonType.BACK,
  private val clickCallback: (ButtonType) -> Unit) {

  fun onButtonClicked() {
    clickCallback.invoke(buttonType)
  }
}