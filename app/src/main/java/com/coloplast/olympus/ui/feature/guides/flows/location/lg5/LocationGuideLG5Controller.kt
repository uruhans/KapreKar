package com.coloplast.olympus.ui.feature.guides.flows.location.lg5

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 05/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LocationGuideLG5Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }){

  override val resourceId: Int = R.drawable.location_guide_permission
  override val titleId: Int = R.string.guide_lg5_title
  override val descriptionId: Int = R.string.guide_lg5_subtitle
  override val position: Int = 4
  override val totalCount: Int = 6
  override val showQuestionFab: Boolean = false

  companion object{
    private const val TAG_CONTROLLER = "location_guide_lg5_controller"

    fun routerTransaction() = RouterTransaction.with(LocationGuideLG5Controller())
      .tag(TAG_CONTROLLER)
  }
}