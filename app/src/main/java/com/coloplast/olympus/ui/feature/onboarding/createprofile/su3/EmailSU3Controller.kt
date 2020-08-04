package com.coloplast.olympus.ui.feature.onboarding.createprofile.su3

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerEmailSu3Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class EmailSU3Controller: BaseController<ControllerEmailSu3Binding>() {

  private var email: String = ""
  private var validEmail: Boolean = false

  val onCurrentEmailChanged: ((String) -> Unit) = {
    email = it
  }

  val onValidityChanged: ((Boolean) -> Unit) = {
    validEmail = it
  }

  override fun attachVariables(viewBinding: ControllerEmailSu3Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerEmailSu3Binding) {
    //noop
  }

  fun onNextClicked(){
    if(validEmail){
      getListener(FlowListener::class.java)!!.onNextClicked(email)
    }
  }

  override fun getLayout(): Int = R.layout.controller_email_su_3

  companion object{
    const val TAG_CONTROLLER = "email_su_3_controller"

    fun getRouterTransaction() = RouterTransaction.with(EmailSU3Controller())
      .tag(TAG_CONTROLLER)
  }
}