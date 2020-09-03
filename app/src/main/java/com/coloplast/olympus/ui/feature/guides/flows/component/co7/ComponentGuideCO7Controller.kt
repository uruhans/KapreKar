package com.coloplast.olympus.ui.feature.guides.flows.component.co7

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ComponentGuideCO7Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(
  MediaGuideController.SCREEN_NAME, TAG_CONTROLLER
) }){

  override val resourceId: Int = R.drawable.placeholder
  override val titleId: Int = R.string.guide_co7_title
  override val descriptionId: Int = R.string.guide_co7_description
  override val position: Int = 6
  override val totalCount: Int = 12

  companion object{
    private const val TAG_CONTROLLER = "component_guide_co7_controller"

    fun routerTransaction() = RouterTransaction.with(ComponentGuideCO7Controller())
      .tag(TAG_CONTROLLER)
  }
}