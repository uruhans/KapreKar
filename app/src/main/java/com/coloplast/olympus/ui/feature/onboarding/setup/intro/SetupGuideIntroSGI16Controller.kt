package com.coloplast.olympus.ui.feature.onboarding.setup.intro

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuideIntroSGI16Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.raw.sg_i16_chargerandcable
  override val titleId: Int = R.string.onboarding_chapter_intro_charging_title
  override val descriptionId: Int = R.string.onboarding_chapter_intro_charging_description
  override val position: Int = 1
  override val totalCount: Int = 2

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_intro_sgi16_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideIntroSGI16Controller())
      .tag(TAG_CONTROLLER)
  }
}