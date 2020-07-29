package com.coloplast.olympus.ui.feature.guides.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.coloplast.olympus.R


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 20/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class GuideCard(
  @DrawableRes val imageId: Int,
  @StringRes val titleId: Int,
  @StringRes val descriptionId: Int
){
  COMPONENT_GUIDE(R.drawable.guide_co_thumbnail, R.string.guides_co_card_title, R.string.guides_co_card_description),
  APP_TOUR(R.drawable.guide_ap_thumbnail, R.string.guides_ap_card_title, R.string.guides_ap_card_description),
  CLEANING_GUIDE(R.drawable.guide_thumbnail_placeholder, R.string.guide_cl_card_title, R.string.guide_cl_card_description),
  DRYING_GUIDE(R.drawable.guide_thumbnail_placeholder, R.string.guide_dg_card_title, R.string.guide_dg_card_description);
}