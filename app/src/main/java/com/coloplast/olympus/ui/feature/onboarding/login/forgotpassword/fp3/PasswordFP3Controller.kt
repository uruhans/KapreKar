package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.fp3

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerPasswordFp3Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class PasswordFP3Controller: BaseController<ControllerPasswordFp3Binding>() {

  private var password: String = ""
  private var validPassword: Boolean = false

  val onCurrentPasswordChanged: ((String) -> Unit) = {
    password = it
  }

  val onValidityChanged: ((Boolean) -> Unit) = {
    validPassword = it
  }

  override fun attachVariables(viewBinding: ControllerPasswordFp3Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerPasswordFp3Binding) {
    //noop
  }

  fun onSetNewPassword(){
    if(validPassword){
      getListener(FlowListener::class.java)!!.onNextClicked(password)
    }
  }

  override fun getLayout(): Int = R.layout.controller_password_fp_3

  companion object{
    const val TAG_CONTROLLER = "password_fp_3_controller"

    fun getRouterTransaction() = RouterTransaction.with(PasswordFP3Controller())
      .tag(TAG_CONTROLLER)
  }
}