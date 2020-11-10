package com.coloplast.olympus.ui.feature.onboarding.setup.applying

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener

class SetupGuideApplyingSGA12Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }),
  TopMenuVisibilityListener {

  override val resourceId: Int = R.raw.sg_a12_attach_tm_flap
  override val titleId: Int = R.string.onboarding_chapter_applying_attach_title
  override val descriptionId: Int = R.string.onboarding_chapter_applying_attach_description
  override val position: Int = 3
  override val totalCount: Int = 3

  override fun showCloseButton(): Boolean {
    return false
  }

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_applying_sga12_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideApplyingSGA12Controller())
      .tag(TAG_CONTROLLER)
  }


}