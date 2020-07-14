package com.coloplast.olympus.ui.feature.onboarding.createprofile.su4

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerPasswordSu4Binding
import com.coloplast.olympus.shared.utils.StringUtils.PASSWORD_REGEX
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.extensions.onValueChanged
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class PasswordSU4Controller: BaseController<ControllerPasswordSu4Binding>() {

  val validPassword = ObservableBoolean(true)
  val password = ObservableField("Test1234!").apply {
    compositeDisposable.add(
      onValueChanged {
        val regex = PASSWORD_REGEX.toRegex()
        val isValid = get()!!.matches(regex)
        validPassword.set(isValid)
      }
    )
  }

  override fun attachVariables(viewBinding: ControllerPasswordSu4Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerPasswordSu4Binding) {
    //noop
  }

  fun onNextClicked(){
    password.notifyChange()
    if(validPassword.get()){
      getListener(FlowListener::class.java)!!.onNextClicked(password.get()!!)
    }
  }

  override fun getLayout(): Int = R.layout.controller_password_su_4

  companion object{
    const val TAG_CONTROLLER = "password_su_4_controller"

    fun getRouterTransaction() = RouterTransaction.with(PasswordSU4Controller())
      .tag(TAG_CONTROLLER)
  }
}