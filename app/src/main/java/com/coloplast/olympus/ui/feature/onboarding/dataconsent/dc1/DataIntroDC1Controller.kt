package com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc1

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerDataIntroDc1Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DataIntroDC1Controller: BaseController<ControllerDataIntroDc1Binding>() {

  override fun attachVariables(viewBinding: ControllerDataIntroDc1Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerDataIntroDc1Binding) {
    //noop
  }

  fun onReadMoreClicked(){
    getListener(FlowListener::class.java)!!.onNextClicked()
  }

  fun onSkipClicked(){

  }

  override fun getLayout(): Int= R.layout.controller_data_intro_dc_1

  companion object{
    private const val TAG_CONTROLLER = "data_intro_dc1_controller"

    fun getRouterTransaction() = RouterTransaction.with(DataIntroDC1Controller())
      .tag(TAG_CONTROLLER)
  }
}