package com.coloplast.olympus.ui.feature.guides.flows.tour.ap8

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AppTourAP8Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }){

  override val resourceId: Int = R.drawable.guide_app_tour_guides
  override val titleId: Int = R.string.guide_ap8_title
  override val descriptionId: Int = R.string.guide_ap8_subtitle
  override val position: Int = 7
  override val totalCount: Int = 11

  companion object{
    private const val TAG_CONTROLLER = "app_tour_ap8_controller"

    fun routerTransaction() = RouterTransaction.with(AppTourAP8Controller())
      .tag(TAG_CONTROLLER)
  }
}