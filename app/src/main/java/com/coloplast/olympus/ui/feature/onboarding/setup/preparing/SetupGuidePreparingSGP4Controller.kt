package com.coloplast.olympus.ui.feature.onboarding.setup.preparing

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuidePreparingSGP4Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.raw.sg_p4_pressdownsl
  override val titleId: Int = R.string.onboarding_chapter_preparing_press_down_title
  override val descriptionId: Int = R.string.onboarding_chapter_preparing_press_down_description
  override val position: Int = 3
  override val totalCount: Int = 4

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_preparing_sgp4_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuidePreparingSGP4Controller())
      .tag(TAG_CONTROLLER)
  }

}