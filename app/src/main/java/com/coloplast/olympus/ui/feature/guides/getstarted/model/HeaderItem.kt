package com.coloplast.olympus.ui.feature.guides.getstarted.model

import androidx.annotation.StringRes

class HeaderItem(@StringRes val header: Int = 0, @StringRes val subHeader : Int = 0){
  val showHeader =  header !=  0
  val showSubHeader =  subHeader != 0
}