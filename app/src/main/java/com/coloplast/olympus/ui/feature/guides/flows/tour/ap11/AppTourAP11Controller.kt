package com.coloplast.olympus.ui.feature.guides.flows.tour.ap11

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AppTourAP11Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }){

  override val resourceId: Int = R.drawable.guide_placeholder
  override val titleId: Int = R.string.guide_ap11_title
  override val descriptionId: Int = R.string.guide_ap11_subtitle
  override val position: Int = 10
  override val totalCount: Int = 11

  companion object{
    private const val TAG_CONTROLLER = "app_tour_ap11_controller"

    fun routerTransaction() = RouterTransaction.with(AppTourAP11Controller())
      .tag(TAG_CONTROLLER)
  }
}