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
  GuideCard.APPLYING_GUIDE -> R.drawable.guide_ag_thumbnail
  GuideCard.DRY_APPLYING_TIPS -> R.drawable.guide_ag_thumbnail
  GuideCard.CONNECTION_GUIDE -> R.drawable.guide_cg_thumbnail
  GuideCard.CHARGING_GUIDE -> R.drawable.guide_ch_thumbnail
  GuideCard.LOCATION_GUIDE -> R.drawable.guide_lg_thumbnail
  GuideCard.CHECK_BATTERY_LEVEL -> R.drawable.guide_cb_thumbnail
  GuideCard.ADD_TRANSMITTER -> R.drawable.guide_at_thumbnail
  GuideCard.DELETE_TRANSMITTER -> R.drawable.guide_dt_thumbnail
}

@StringRes fun GuideCard.getTitle(): Int = when (this) {
  GuideCard.COMPONENT_GUIDE -> R.string.guides_co_card_title
  GuideCard.APP_TOUR -> R.string.guides_ap_card_title
  GuideCard.APPLYING_GUIDE -> R.string.guides_ag_card_title
  GuideCard.DRY_APPLYING_TIPS -> R.string.guides_da_card_title
  GuideCard.CONNECTION_GUIDE -> R.string.guides_cg_card_title
  GuideCard.CHARGING_GUIDE -> R.string.guides_ch_card_title
  GuideCard.LOCATION_GUIDE -> R.string.guides_lg_card_title
  GuideCard.CHECK_BATTERY_LEVEL -> R.string.guides_cb_card_title
  GuideCard.ADD_TRANSMITTER -> R.string.guides_at_card_title
  GuideCard.DELETE_TRANSMITTER -> R.string.guides_dt_card_title
}


@StringRes fun GuideCard.getDescription(): Int = when (this) {
  GuideCard.COMPONENT_GUIDE -> R.string.guides_co_card_description
  GuideCard.APP_TOUR -> R.string.guides_ap_card_description
  GuideCard.APPLYING_GUIDE -> R.string.guides_ag_card_description
  GuideCard.DRY_APPLYING_TIPS -> R.string.guides_da_card_description
  GuideCard.CONNECTION_GUIDE -> R.string.guides_cg_card_description
  GuideCard.CHARGING_GUIDE -> R.string.guides_ch_card_description
  GuideCard.LOCATION_GUIDE -> R.string.guides_lg_card_description
  GuideCard.CHECK_BATTERY_LEVEL -> R.string.guides_cb_card_description
  GuideCard.ADD_TRANSMITTER -> R.string.guides_at_card_description
  GuideCard.DELETE_TRANSMITTER -> R.string.guides_dt_card_description
}