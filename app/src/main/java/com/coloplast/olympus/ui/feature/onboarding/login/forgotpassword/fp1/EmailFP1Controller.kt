package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.fp1

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerEmailFp1Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class EmailFP1Controller: BaseController<ControllerEmailFp1Binding>(),TopMenuVisibilityListener {

  private var email: String = ""
  private var validEmail: Boolean = false

  val onCurrentEmailChanged: ((String) -> Unit) = {
    email = it
  }

  val onValidityChanged: ((Boolean) -> Unit) = {
    validEmail = it
  }

  override fun attachVariables(viewBinding: ControllerEmailFp1Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerEmailFp1Binding) {
    //noop
  }

  fun onNextClicked(){
    if(validEmail){
      getListener(FlowListener::class.java)!!.onNextClicked(email)
    }
  }

  override fun showCloseButton(): Boolean = false

  override fun getLayout(): Int = R.layout.controller_email_fp_1

  companion object{
    const val TAG_CONTROLLER = "email_fp_1_controller"

    fun getRouterTransaction() = RouterTransaction.with(EmailFP1Controller())
      .tag(TAG_CONTROLLER)
  }
}