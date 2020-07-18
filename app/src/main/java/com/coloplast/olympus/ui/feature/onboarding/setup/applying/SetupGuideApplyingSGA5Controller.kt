package com.coloplast.olympus.ui.feature.onboarding.setup.applying

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController

class SetupGuideApplyingSGA5Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) {

  override val resourceId: Int = R.raw.sg_a4_takeouttm // this is out of "sync"
  override val titleId: Int = R.string.onboarding_chapter_applying_take_out_title
  override val descriptionId: Int = R.string.onboarding_chapter_applying_take_out_description
  override val position: Int = 1
  override val totalCount: Int = 3

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_applying_sga5_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideApplyingSGA5Controller())
      .tag(TAG_CONTROLLER)
  }
}