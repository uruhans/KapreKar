package com.coloplast.olympus.ui.feature.onboarding.setup.intro

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuideIntroSGI13Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.raw.sg_i13_tm_opening
  override val titleId: Int = R.string.guide_co9_title
  override val descriptionId: Int = R.string.guide_co9_description
  override val position: Int = 3
  override val totalCount: Int = 5

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_intro_sgi13_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideIntroSGI13Controller())
      .tag(TAG_CONTROLLER)
  }
}