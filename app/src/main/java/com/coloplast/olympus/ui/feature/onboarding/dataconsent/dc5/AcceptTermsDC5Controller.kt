package com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc5

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAcceptTermsDc5Binding
import com.coloplast.olympus.ui.common.BaseController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AcceptTermsDC5Controller: BaseController<ControllerAcceptTermsDc5Binding>() {


  override fun attachVariables(viewBinding: ControllerAcceptTermsDc5Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerAcceptTermsDc5Binding) {
    //noop
  }

  fun onAcceptClicked(){}

  fun onDeclineClicked(){}

  override fun getLayout(): Int = R.layout.controller_accept_terms_dc_5

  companion object{
    private const val TAG_CONTROLLER = "accept_terms_dc5_controller"

    fun getRouterTransaction() = RouterTransaction.with(AcceptTermsDC5Controller())
      .tag(TAG_CONTROLLER)
  }
}