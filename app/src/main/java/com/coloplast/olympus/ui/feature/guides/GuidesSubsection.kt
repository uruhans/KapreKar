package com.coloplast.olympus.ui.feature.guides

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuidesSubsectionBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.types.GuidesMenuSubsection

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 20/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class GuidesSubsection(args: Bundle): BaseController<ControllerGuidesSubsectionBinding>(args) {

  val type: GuidesMenuSubsection = args.get(GUIDE_TYPE) as GuidesMenuSubsection

  override fun attachVariables(viewBinding: ControllerGuidesSubsectionBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerGuidesSubsectionBinding) {
    //noop
  }

  override fun getLayout(): Int = R.layout.controller_guides_subsection

  companion object {
    const val TAG_CONTROLLER = "guides_subsection_controller"
    const val GUIDE_TYPE = "GUIDE_TYPE"

    fun getRouterTransaction(guidesMenuSubsection: GuidesMenuSubsection): RouterTransaction{
      val args = Bundle().apply {
        putSerializable(GUIDE_TYPE, guidesMenuSubsection)
      }
      return RouterTransaction.with(GuidesSubsection(args))
        .tag(TAG_CONTROLLER)
    }
  }
}