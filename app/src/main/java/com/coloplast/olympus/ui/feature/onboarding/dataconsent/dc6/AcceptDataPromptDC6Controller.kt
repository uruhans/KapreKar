package com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc6

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAcceptDataPromptDc6Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AcceptDataPromptDC6Controller: BaseController<ControllerAcceptDataPromptDc6Binding>() {

  override fun attachVariables(viewBinding: ControllerAcceptDataPromptDc6Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerAcceptDataPromptDc6Binding) {
    //noop
  }

  fun onGoBack(){
    getListener(FlowListener::class.java)!!.onPreviousClicked()
  }

  fun onContinue(){
    getListener(FlowListener::class.java)!!.onNextClicked()
  }

  override fun getLayout(): Int= R.layout.controller_accept_data_prompt_dc6

  companion object{
    private const val TAG_CONTROLLER = "accept_data_prompt_dc6_controller"

    fun getRouterTransaction() = RouterTransaction.with(AcceptDataPromptDC6Controller())
      .tag(TAG_CONTROLLER)
  }
}