package com.coloplast.olympus.ui.feature.profile

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerProfileBinding
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.feature.home.HomeController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 07/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ProfileController: CustomToolbarController<ControllerProfileBinding>() {

  override fun attachVariables(viewBinding: ControllerProfileBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerProfileBinding) {
    super.onViewBound(viewBinding)
    //noop
  }

  override fun getLayout(): Int = R.layout.controller_profile

  override fun getNavigationIconResId(): Int = R.drawable.ic_arrow

  companion object {
    const val TAG_CONTROLLER = "profile_controller"

    fun routerTransaction() = RouterTransaction.with(ProfileController())
      .tag(HomeController.TAG_CONTROLLER)
  }
}