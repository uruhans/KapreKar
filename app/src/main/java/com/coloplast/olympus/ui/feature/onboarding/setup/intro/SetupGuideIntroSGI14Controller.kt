package com.coloplast.olympus.ui.feature.onboarding.setup.intro

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuideIntroSGI14Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.raw.sg_i14_disengaging_tm_lid
  override val titleId: Int = R.string.guide_co10_title
  override val descriptionId: Int = R.string.guide_co10_description
  override val position: Int = 4
  override val totalCount: Int = 5

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_intro_sgi14_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideIntroSGI14Controller())
      .tag(TAG_CONTROLLER)
  }
}