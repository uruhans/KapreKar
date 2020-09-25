package com.coloplast.olympus.ui.feature.onboarding.termsofuse

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTermsOfUseBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.WebViewController
import com.coloplast.olympus.ui.common.listeners.FlowListener

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 25/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class TermsOfUseTUController: BaseController<ControllerTermsOfUseBinding>() {

  override fun attachVariables(viewBinding: ControllerTermsOfUseBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerTermsOfUseBinding) {
    //noop
  }

  fun onLinkCLicked(){
    val url = applicationContext!!.getString(R.string.terms_of_use_link)
    getContentRouter().pushController(
      WebViewController.getRouterTransaction(url)
        .popChangeHandler(VerticalChangeHandler())
        .pushChangeHandler(VerticalChangeHandler())
    )
  }

  fun onContinueClicked(){
    getListener(FlowListener::class.java)!!.onNextClicked()
  }

  override fun getLayout(): Int = R.layout.controller_terms_of_use

  companion object{

    private const val CONTROLLER_TAG = "terms_of_use_controller"

    fun routerTransaction() = RouterTransaction.with(TermsOfUseTUController())
      .tag(CONTROLLER_TAG)
  }
}