package com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su7

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerInsuranceSu7Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.extensions.onValueChanged
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.types.HealthInsurance

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class InsuranceSU7Controller: BaseController<ControllerInsuranceSu7Binding>() {

  val insuranceValues by lazy {
    HealthInsurance.values()
      .map { applicationContext!!.getString(it.stringId) }
  }
  val insuranceSelected = ObservableBoolean(true)
  val insuranceSpinnerPosition = ObservableInt(-1).apply {
    compositeDisposable.add(
      onValueChanged { insuranceSelected.set(true) }
    )
  }

  override fun attachVariables(viewBinding: ControllerInsuranceSu7Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerInsuranceSu7Binding) {
    //noop
  }

  fun onNextClicked(){
    insuranceSelected.set(insuranceSpinnerPosition.get() >= 0)
    if(insuranceSelected.get()){
      val healthInsurance = HealthInsurance.fromOrdinal(insuranceSpinnerPosition.get())
      getListener(FlowListener::class.java)!!.onNextClicked(healthInsurance)
    }
  }

  override fun getLayout(): Int = R.layout.controller_insurance_su_7

  companion object{
    const val TAG_CONTROLLER = "insurance_su7_controller"

    fun getRouterTransaction() = RouterTransaction.with(InsuranceSU7Controller())
      .tag(TAG_CONTROLLER)
  }
}