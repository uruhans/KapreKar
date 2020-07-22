package com.coloplast.olympus.ui.feature.onboarding.createprofile.su6

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerPhoneSu6Binding
import com.coloplast.olympus.shared.extensions.isValidPhone
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.extensions.onValueChanged
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class PhoneSU6Controller: BaseController<ControllerPhoneSu6Binding>() {

  val validPhone = ObservableBoolean(true)
  val phone = ObservableField("").apply {
    compositeDisposable.add(
      onValueChanged { validPhone.set(this.get()!!.isValidPhone()) }
    )
  }

  override fun attachVariables(viewBinding: ControllerPhoneSu6Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerPhoneSu6Binding) {
    //noop
  }

  fun onNextClicked(){
    phone.notifyChange()
    if(validPhone.get()){
      getListener(FlowListener::class.java)!!.onNextClicked(phone.get()!!)
    }
  }

  override fun getLayout(): Int = R.layout.controller_phone_su_6

  companion object{
    const val TAG_CONTROLLER = "phone_su_6_controller"

    fun getRouterTransaction() = RouterTransaction.with(PhoneSU6Controller())
      .tag(TAG_CONTROLLER)
  }
}