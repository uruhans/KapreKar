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
  CONNECTION_GUIDE(R.drawable.guide_cg_thumbnail, R.string.guides_cg_card_title, R.string.guides_cg_card_description),
  CHARGING_GUIDE(R.drawable.guide_ch_thumbnail, R.string.guides_ch_card_title, R.string.guides_ch_card_description),
  LOCATION_GUIDE(R.drawable.guide_lg_thumbnail, R.string.guides_lg_card_title, R.string.guides_lg_card_description),
  CHECK_BATTERY_LEVEL(R.drawable.guide_cb_thumbnail, R.string.guides_cb_card_title, R.string.guides_cb_card_description),
  ADD_TRANSMITTER(R.drawable.guide_at_thumbnail, R.string.guides_at_card_title, R.string.guides_at_card_description),
  DELETE_TRANSMITTER(R.drawable.guide_dt_thumbnail, R.string.guides_dt_card_title, R.string.guides_dt_card_description);
}