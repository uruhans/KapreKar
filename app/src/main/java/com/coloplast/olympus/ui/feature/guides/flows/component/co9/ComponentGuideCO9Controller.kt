package com.coloplast.olympus.ui.feature.guides.flows.component.co9

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ComponentGuideCO9Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(
  MediaGuideController.SCREEN_NAME, TAG_CONTROLLER
) }){

  override val resourceId: Int = R.drawable.placeholder
  override val titleId: Int = R.string.guide_co9_title
  override val descriptionId: Int = R.string.guide_co9_description
  override val position: Int = 8
  override val totalCount: Int = 12

  companion object{
    private const val TAG_CONTROLLER = "component_guide_co9_controller"

    fun routerTransaction() = RouterTransaction.with(ComponentGuideCO9Controller())
      .tag(TAG_CONTROLLER)
  }
}