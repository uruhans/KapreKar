package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.fp2

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSecurityCodeFp2Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.common.widgets.InputCodeEditText
import com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword.ForgotPasswordView


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SecurityCodeFP2Controller(args: Bundle): BaseController<ControllerSecurityCodeFp2Binding>(args),
  InputCodeEditText.Listener {

  val email = args.getString(EMAIL)!!

  override fun attachVariables(viewBinding: ControllerSecurityCodeFp2Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSecurityCodeFp2Binding) {
    //noop
  }

  override fun onCodeInput(code: String) {
    getListener(FlowListener::class.java)!!.onNextClicked(code)
  }

  fun onResendCode(){
    getListener(ForgotPasswordView::class.java)!!.resendCode()
  }

  override fun getLayout(): Int = R.layout.controller_security_code_fp_2

  companion object{
    const val TAG_CONTROLLER = "security_code_fp_2_controller"
    const val EMAIL = "EMAIL"

    fun getRouterTransaction(email: String): RouterTransaction{
      val args = Bundle().apply {
        putString(EMAIL, email)
      }

      return RouterTransaction.with(SecurityCodeFP2Controller(args))
        .tag(TAG_CONTROLLER)
    }
  }
}