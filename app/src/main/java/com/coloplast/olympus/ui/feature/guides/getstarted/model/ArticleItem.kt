package com.coloplast.olympus.ui.feature.guides.getstarted.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


data class ArticleItem (
  @StringRes val title: Int = 0,
  @DrawableRes val image: Int = 0,
  @StringRes val description: Int = 0) {
  val showImage = image != 0
  val showTitle = title != 0
  val showDescription = description != 0
}