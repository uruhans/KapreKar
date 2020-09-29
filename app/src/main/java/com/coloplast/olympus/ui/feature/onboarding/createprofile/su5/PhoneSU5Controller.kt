package com.coloplast.olympus.ui.feature.onboarding.createprofile.su5

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerPhoneSu5Binding
import com.coloplast.olympus.shared.extensions.isValidPhone
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.extensions.onValueChanged
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class PhoneSU5Controller: BaseController<ControllerPhoneSu5Binding>() {

  val prefixSpinnerPosition = ObservableInt(0)
  val validPhone = ObservableBoolean(true)
  val phone = ObservableField("").apply {
    compositeDisposable.add(
      onValueChanged { validPhone.set(this.get()!!.isValidPhone()) }
    )
  }

  override fun attachVariables(viewBinding: ControllerPhoneSu5Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerPhoneSu5Binding) {
    //noop
  }

  fun onNextClicked(){
    phone.notifyChange()
    if(validPhone.get()){
      val prefix = applicationContext!!.resources.getStringArray(R.array.prefixes)[prefixSpinnerPosition.get()]
      val phoneWithPrefix = prefix + phone.get()
      getListener(FlowListener::class.java)!!.onNextClicked(phoneWithPrefix)
    }
  }

  override fun getLayout(): Int = R.layout.controller_phone_su_5

  companion object{
    const val TAG_CONTROLLER = "phone_su_5_controller"

    fun getRouterTransaction() = RouterTransaction.with(PhoneSU5Controller())
      .tag(TAG_CONTROLLER)
  }
}