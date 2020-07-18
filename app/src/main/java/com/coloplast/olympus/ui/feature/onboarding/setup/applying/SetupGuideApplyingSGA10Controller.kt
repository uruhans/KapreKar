package com.coloplast.olympus.ui.feature.onboarding.setup.applying

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuideApplyingSGA10Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.raw.sg_a6_locatetmopening // this is out of "sync"
  override val titleId: Int = R.string.onboarding_chapter_applying_locating_opening_title
  override val descriptionId: Int = R.string.onboarding_chapter_applying_locating_opening_description
  override val position: Int = 1
  override val totalCount: Int = 4

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_applying_sga10_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideApplyingSGA10Controller())
      .tag(TAG_CONTROLLER)
  }
}