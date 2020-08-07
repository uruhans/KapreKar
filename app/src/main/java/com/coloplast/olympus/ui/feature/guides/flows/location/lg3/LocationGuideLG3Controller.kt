package com.coloplast.olympus.ui.feature.guides.flows.location.lg2

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 05/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LocationGuideLG3Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(
  MediaGuideController.SCREEN_NAME, TAG_CONTROLLER) }){

  override val resourceId: Int = R.drawable.placeholder
  override val titleId: Int = R.string.guide_lg3_title
  override val descriptionId: Int = R.string.guide_lg3_subtitle
  override val position: Int = 2
  override val totalCount: Int = 6
  override val showQuestionFab: Boolean = false

  companion object{
    private const val TAG_CONTROLLER = "location_guide_lg2_controller"

    fun routerTransaction() = RouterTransaction.with(LocationGuideLG3Controller())
      .tag(TAG_CONTROLLER)
  }
}