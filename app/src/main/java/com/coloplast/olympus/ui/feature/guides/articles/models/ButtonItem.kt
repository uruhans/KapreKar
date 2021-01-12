package com.coloplast.olympus.ui.feature.guides.articles.models

import androidx.annotation.StringRes
import com.coloplast.olympus.ui.feature.guides.models.ButtonType

class ButtonItem(
  @StringRes val text: Int,
  val buttonType: ButtonType = ButtonType.BACK,
  private val clickCallback: (ButtonType, Int) -> Unit) {

  fun onButtonClicked() {
    clickCallback.invoke(buttonType, text)
  }
}