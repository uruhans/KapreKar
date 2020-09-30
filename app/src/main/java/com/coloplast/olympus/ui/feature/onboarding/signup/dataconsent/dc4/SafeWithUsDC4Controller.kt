package com.coloplast.olympus.ui.feature.onboarding.signup.dataconsent.dc4

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSafeWithUsDc4Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SafeWithUsDC4Controller: BaseController<ControllerSafeWithUsDc4Binding>() {

  val position = 3
  val totalCount = 3

  override fun attachVariables(viewBinding: ControllerSafeWithUsDc4Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSafeWithUsDc4Binding) {
    //noop
  }

  fun onNextClicked(){
    getListener(FlowListener::class.java)!!.onNextClicked()
  }

  override fun getLayout(): Int = R.layout.controller_safe_with_us_dc_4

  companion object{
    private const val TAG_CONTROLLER = "safe_with_us_dc4_controller"

    fun getRouterTransaction() = RouterTransaction.with(SafeWithUsDC4Controller())
      .tag(TAG_CONTROLLER)
  }
}