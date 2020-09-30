package com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su4

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerPasswordSu4Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class PasswordSU4Controller: BaseController<ControllerPasswordSu4Binding>() {

  private var password: String = ""
  private var validPassword: Boolean = false

  val onCurrentPasswordChanged: ((String) -> Unit) = {
    password = it
  }

  val onValidityChanged: ((Boolean) -> Unit) = {
    validPassword = it
  }

  override fun attachVariables(viewBinding: ControllerPasswordSu4Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerPasswordSu4Binding) {
    //noop
  }

  fun onNextClicked(){
    if(validPassword){
      getListener(FlowListener::class.java)!!.onNextClicked(password)
    }
  }

  override fun getLayout(): Int = R.layout.controller_password_su_4

  companion object{
    const val TAG_CONTROLLER = "password_su_4_controller"

    fun getRouterTransaction() = RouterTransaction.with(PasswordSU4Controller())
      .tag(TAG_CONTROLLER)
  }
}