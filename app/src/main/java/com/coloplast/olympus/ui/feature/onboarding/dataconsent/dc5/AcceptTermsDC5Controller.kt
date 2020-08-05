package com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc5

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAcceptTermsDc5Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.WebViewController
import com.coloplast.olympus.ui.common.listeners.FlowListener


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

  fun onPolicyAgreementClicked(){
    val url = applicationContext!!.getString(R.string.onboarding_dc5_link1)
    getContentRouter().pushController(
      WebViewController.getRouterTransaction(url)
        .popChangeHandler(VerticalChangeHandler())
        .pushChangeHandler(VerticalChangeHandler())
    )
  }

  fun onTermsOfUse(){
    val url = applicationContext!!.getString(R.string.onboarding_dc5_link2)
    getContentRouter().pushController(
      WebViewController.getRouterTransaction(url)
        .popChangeHandler(VerticalChangeHandler())
        .pushChangeHandler(VerticalChangeHandler())
    )
  }

  fun onAcceptClicked(){
    getListener(FlowListener::class.java)!!.onNextClicked()
  }

  fun onDeclineClicked(){
    //TODO
  }

  override fun getLayout(): Int = R.layout.controller_accept_terms_dc_5

  companion object{
    private const val TAG_CONTROLLER = "accept_terms_dc5_controller"

    fun getRouterTransaction() = RouterTransaction.with(AcceptTermsDC5Controller())
      .tag(TAG_CONTROLLER)
  }
}