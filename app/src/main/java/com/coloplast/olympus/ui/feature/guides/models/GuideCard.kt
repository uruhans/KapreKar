package com.coloplast.olympus.ui.feature.guides.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.RouterTransaction


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 20/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
data class GuideCard(
  @DrawableRes val imageId: Int,
  @StringRes val titleId: Int,
  @StringRes val descriptionId: Int,
  val destination: RouterTransaction
)