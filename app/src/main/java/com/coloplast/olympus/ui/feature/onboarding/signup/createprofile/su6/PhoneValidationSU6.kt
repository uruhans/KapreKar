package com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su6

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSecurityCodeSu6Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.common.widgets.InputCodeEditText
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.CreateUserFlowView


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 22/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class PhoneValidationSU6: BaseController<ControllerSecurityCodeSu6Binding>(),
  InputCodeEditText.Listener {


  override fun attachVariables(viewBinding: ControllerSecurityCodeSu6Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSecurityCodeSu6Binding) {
    //noop
  }

  override fun onCodeInput(code: String) {
    getListener(FlowListener::class.java)!!.onNextClicked(code)
  }

  fun onResendCode(){
    getListener(CreateUserFlowView::class.java)!!.sendCode()
  }

  override fun getLayout(): Int = R.layout.controller_security_code_su6

  companion object{
    const val TAG_CONTROLLER = "security_code_su_6_controller"

    fun getRouterTransaction(): RouterTransaction {
      return RouterTransaction.with(PhoneValidationSU6())
        .tag(TAG_CONTROLLER)
    }
  }
}