package com.coloplast.olympus.ui.feature.onboarding.termsofuse

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTermsOfUseBinding
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.WebViewController

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 25/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class TermsOfUseTUController: CustomToolbarController<ControllerTermsOfUseBinding>() {

  override fun attachVariables(viewBinding: ControllerTermsOfUseBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
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
    //getListener(Flow)
  }

  override fun getLayout(): Int = R.layout.controller_terms_of_use

  override fun getNavigationIconResId(): Int = R.drawable.ic_arrow

  companion object{

    private const val CONTROLLER_TAG = "terms_of_use_controller"

    fun routerTransaction() = RouterTransaction.with(TermsOfUseTUController())
      .tag(CONTROLLER_TAG)
  }
}