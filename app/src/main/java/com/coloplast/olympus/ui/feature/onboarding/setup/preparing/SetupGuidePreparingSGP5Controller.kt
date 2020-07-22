package com.coloplast.olympus.ui.feature.onboarding.setup.preparing

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuidePreparingSGP5Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.raw.sg_p5_ensuresecureadhesion
  override val titleId: Int = R.string.onboarding_chapter_preparing_secure_title
  override val descriptionId: Int = R.string.onboarding_chapter_preparing_secure_description
  override val position: Int = 4
  override val totalCount: Int = 4

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_preparing_sgp5_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuidePreparingSGP5Controller())
      .tag(TAG_CONTROLLER)
  }

}