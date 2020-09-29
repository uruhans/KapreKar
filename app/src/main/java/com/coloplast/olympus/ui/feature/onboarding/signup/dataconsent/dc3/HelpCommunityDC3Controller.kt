package com.coloplast.olympus.ui.feature.onboarding.signup.dataconsent.dc3

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerHelpCommunityDc3Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class HelpCommunityDC3Controller: BaseController<ControllerHelpCommunityDc3Binding>() {

  val position = 2
  val totalCount = 3

  override fun attachVariables(viewBinding: ControllerHelpCommunityDc3Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerHelpCommunityDc3Binding) {
    //noop
  }

  fun onNextClicked(){
    getListener(FlowListener::class.java)!!.onNextClicked()
  }

  override fun getLayout(): Int = R.layout.controller_help_community_dc_3

  companion object{
    private const val TAG_CONTROLLER = "help_community_dc3_controller"

    fun getRouterTransaction() = RouterTransaction.with(HelpCommunityDC3Controller())
      .tag(TAG_CONTROLLER)
  }
}