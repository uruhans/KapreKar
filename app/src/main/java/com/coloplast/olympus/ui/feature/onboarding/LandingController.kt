package com.coloplast.olympus.ui.feature.onboarding

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerLandingBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.onboarding.createprofile.CreateUserFlowController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 30/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LandingController: BaseController<ControllerLandingBinding>() {
  override fun attachVariables(viewBinding: ControllerLandingBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerLandingBinding) {
    //noop
  }

  fun onCreateProfileClicked(){
    getContentRouter().pushController(CreateUserFlowController.routerTransaction()
      .popChangeHandler(HorizontalChangeHandler())
      .pushChangeHandler(HorizontalChangeHandler())
    )

  }

  fun onLogInClicked(){
    //TODO: Link with CPO-520
  }

  override fun getLayout(): Int = R.layout.controller_landing

  companion object{
    private const val TAG_CONTROLLER = "landing_controller"

    fun routerTransaction() = RouterTransaction.with(LandingController())
      .tag(TAG_CONTROLLER)
  }
}