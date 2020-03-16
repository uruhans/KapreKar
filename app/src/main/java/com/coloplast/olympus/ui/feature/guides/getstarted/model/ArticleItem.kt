package com.coloplast.olympus.ui.feature.guides.getstarted.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


data class ArticleItem (
  @DrawableRes val image: Int? = null,
  @StringRes val title: Int? = null,
  @StringRes val description: Int? = null,
  val showImage: Boolean = true,
  val showTitle: Boolean = true,
  val showDescription: Boolean = false)