package com.coloplast.olympus.ui.feature.about

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAboutBinding
import com.coloplast.olympus.ui.common.CustomToolbarController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AboutController: CustomToolbarController<ControllerAboutBinding>() {

  override fun attachVariables(viewBinding: ControllerAboutBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun getLayout(): Int = R.layout.controller_about

  override fun getNavigationIconResId(): Int = R.drawable.ic_arrow

  companion object{

    private const val CONTROLLER_TAG = "about_controller"

    fun routerTransaction() = RouterTransaction.with(AboutController())
      .tag(CONTROLLER_TAG)
  }
}