package com.coloplast.olympus.ui.feature.onboarding.setup.applying

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener

class SetupGuideApplyingSGA6Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }), TopMenuVisibilityListener {

  override val resourceId: Int = R.raw.sg_a_tmh3_ensuretmclosed // this is out of "sync"
  override val titleId: Int = R.string.onboarding_chapter_applying_ensure_closed_title
  override val descriptionId: Int = R.string.onboarding_chapter_applying_ensure_closed_description
  override val position: Int = 2
  override val totalCount: Int = 3

  override fun showCloseButton(): Boolean {
    return false
  }

  companion object {
    const val TAG_CONTROLLER = "setup_guide_applying_sga6_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideApplyingSGA6Controller())
      .tag(TAG_CONTROLLER)
  }
}