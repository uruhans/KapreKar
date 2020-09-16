package com.coloplast.olympus.ui.feature.onboarding.setup.intro

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuideIntroSGI6Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.drawable.sg_i6_whatsinthebox
  override val titleId: Int = R.string.onboarding_chapter_intro_box_title
  override val descriptionId: Int = R.string.onboarding_chapter_intro_box_description
  override val position: Int = 0
  override val totalCount: Int = 0

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_intro_sgi6_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideIntroSGI6Controller())
      .tag(TAG_CONTROLLER)
  }
}