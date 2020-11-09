package com.coloplast.olympus.ui.feature.onboarding.setup.intro

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuideIntroSGI11Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.drawable.sg_i11_yourtransmitter
  override val titleId: Int = R.string.onboarding_chapter_intro_transmitter_title
  override val descriptionId: Int = R.string.onboarding_chapter_intro_transmitter_description
  override val position: Int = 1
  override val totalCount: Int = 5

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_intro_sgi11_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideIntroSGI11Controller())
      .tag(TAG_CONTROLLER)
  }
}