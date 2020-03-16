package com.coloplast.olympus.ui.feature.guides.getstarted.model

import androidx.annotation.StringRes

class HeaderItem(@StringRes val header: Int? = null, @StringRes val subHeader : Int? = null, val showHeader : Boolean =  true,  val showSubHeader : Boolean =  true)