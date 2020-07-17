package com.coloplast.olympus.ui.feature.guides

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuidesMenuBinding
import com.coloplast.olympus.ui.common.BaseController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 17/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class GuideMenuController: BaseController<ControllerGuidesMenuBinding>() {
  override fun attachVariables(viewBinding: ControllerGuidesMenuBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerGuidesMenuBinding) {
    //noop
  }

  override fun getLayout(): Int = R.layout.controller_guides_menu

  companion object{
    private const val CONTROLLER_TAG = "guides_menu_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(GuideMenuController())
        .tag(CONTROLLER_TAG)
    }
  }
}