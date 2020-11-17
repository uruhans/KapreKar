package com.coloplast.olympus.ui.feature.guides.flows.tour.ap4

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AppTourAP4Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }){

  override val resourceId: Int = R.drawable.guide_app_tour_outer_sensor_leak_status
  override val titleId: Int = R.string.guide_ap4_title
  override val descriptionId: Int = R.string.guide_ap4_subtitle
  override val position: Int = 3
  override val totalCount: Int = 11

  companion object{
    private const val TAG_CONTROLLER = "app_tour_ap4_controller"

    fun routerTransaction() = RouterTransaction.with(AppTourAP4Controller())
      .tag(TAG_CONTROLLER)
  }
}