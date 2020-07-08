package com.coloplast.olympus.ui.feature.onboarding.login

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerLoginBinding
import com.coloplast.olympus.ui.common.BaseController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 07/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LoginController: BaseController<ControllerLoginBinding>() {

  val error = ObservableBoolean(true)
  val email = ObservableField("")
  val password = ObservableField("")

  override fun attachVariables(viewBinding: ControllerLoginBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerLoginBinding) {
    //noop
  }

  fun onNextClicked(){

  }

  override fun getLayout(): Int = R.layout.controller_login

  companion object{
    private const val TAG_CONTROLLER = "login_controller"

    fun getRouterTransaction() = RouterTransaction.with(LoginController())
      .tag(TAG_CONTROLLER)
  }
}