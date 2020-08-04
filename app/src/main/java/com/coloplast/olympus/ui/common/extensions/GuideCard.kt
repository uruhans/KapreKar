package com.coloplast.olympus.ui.common.extensions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.feature.guides.models.GuideCard


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 04/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */

@DrawableRes fun GuideCard.getImage(): Int = when(this){
  GuideCard.COMPONENT_GUIDE -> R.drawable.guide_co_thumbnail
  GuideCard.APP_TOUR -> R.drawable.guide_ap_thumbnail
  GuideCard.CLEANING_GUIDE -> R.drawable.guide_thumbnail_placeholder
  GuideCard.DRYING_GUIDE -> R.drawable.guide_thumbnail_placeholder
}

@StringRes fun GuideCard.getTitle(): Int = when(this){
    GuideCard.COMPONENT_GUIDE -> R.string.guides_co_card_title
    GuideCard.APP_TOUR -> R.string.guides_ap_card_title
    GuideCard.CLEANING_GUIDE -> R.string.guide_cl_card_title
    GuideCard.DRYING_GUIDE -> R.string.guide_dg_card_title
  }


@StringRes fun GuideCard.getDescription(): Int = when(this){
  GuideCard.COMPONENT_GUIDE -> R.string.guides_co_card_description
  GuideCard.APP_TOUR -> R.string.guides_ap_card_description
  GuideCard.CLEANING_GUIDE -> R.string.guide_cl_card_description
  GuideCard.DRYING_GUIDE -> R.string.guide_dg_card_description
}