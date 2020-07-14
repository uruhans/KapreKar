package com.coloplast.olympus.ui.feature.onboarding.setup.preparing

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuidePreparingSGP3Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.raw.sg_p3_placeslonbp
  override val titleId: Int = R.string.onboarding_chapter_preparing_place_sl_title
  override val descriptionId: Int = R.string.onboarding_chapter_preparing_place_sl_description
  override val position: Int = 2
  override val totalCount: Int = 4

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_preparing_sgp3_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuidePreparingSGP3Controller())
      .tag(TAG_CONTROLLER)
  }

}