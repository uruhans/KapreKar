package com.coloplast.olympus.ui.feature.guides.flows.component.co12

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ComponentGuideCO12Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(
  SCREEN_NAME, TAG_CONTROLLER
) }){

  override val resourceId: Int = R.raw.sg_i16_chargerandcable
  override val titleId: Int = R.string.guide_co12_title
  override val descriptionId: Int = R.string.guide_co12_description
  override val position: Int = 11
  override val totalCount: Int = 12

  companion object{
    private const val TAG_CONTROLLER = "component_guide_co12_controller"

    fun routerTransaction() = RouterTransaction.with(ComponentGuideCO12Controller())
      .tag(TAG_CONTROLLER)
  }
}