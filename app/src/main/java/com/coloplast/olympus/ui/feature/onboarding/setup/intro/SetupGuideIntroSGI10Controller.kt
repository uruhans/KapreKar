package com.coloplast.olympus.ui.feature.onboarding.setup.intro

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuideIntroSGI10Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.raw.sg_i10_connectorflap
  override val titleId: Int = R.string.onboarding_chapter_intro_connector_flap_title
  override val descriptionId: Int = R.string.onboarding_chapter_intro_connector_flap_description
  override val position: Int = 4
  override val totalCount: Int = 4

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_intro_sgi10_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideIntroSGI10Controller())
      .tag(TAG_CONTROLLER)
  }
}