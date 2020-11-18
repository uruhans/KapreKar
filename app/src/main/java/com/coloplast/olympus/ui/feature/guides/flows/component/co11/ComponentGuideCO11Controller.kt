package com.coloplast.olympus.ui.feature.guides.flows.component.co11

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ComponentGuideCO11Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(
  SCREEN_NAME, TAG_CONTROLLER
) }){

  override val resourceId: Int = R.raw.sg_i15_closing_tm_lid
  override val titleId: Int = R.string.guide_co11_title
  override val descriptionId: Int = R.string.guide_co11_description
  override val position: Int = 10
  override val totalCount: Int = 12

  companion object{
    private const val TAG_CONTROLLER = "component_guide_co11_controller"

    fun routerTransaction() = RouterTransaction.with(ComponentGuideCO11Controller())
      .tag(TAG_CONTROLLER)
  }
}