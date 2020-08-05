package com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc2

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerHelpYouDc2Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class HelpYouDC2Controller: BaseController<ControllerHelpYouDc2Binding>() {

  val position = 1
  val totalCount = 3

  override fun attachVariables(viewBinding: ControllerHelpYouDc2Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerHelpYouDc2Binding) {
    //noop
  }

  fun onNextClicked(){
    getListener(FlowListener::class.java)!!.onNextClicked()
  }

  override fun getLayout(): Int = R.layout.controller_help_you_dc_2

  companion object{
    private const val TAG_CONTROLLER = "help_you_dc2_controller"

    fun getRouterTransaction() = RouterTransaction.with(HelpYouDC2Controller())
      .tag(TAG_CONTROLLER)
  }
}