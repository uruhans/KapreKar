package com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su2

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStomaInfoSu2Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.extensions.onValueChanged
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.common.widgets.SpinnerView
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.models.SU2Model
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.types.StomaSurgeryDate
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.types.StomaType


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 01/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class StomaInfoSU2Controller: BaseController<ControllerStomaInfoSu2Binding>(), SpinnerView.Listener {

  val stomaTypeValues by lazy {
    StomaType.values()
      .map { applicationContext!!.getString(it.stringId) }
  }
  val stomaTypeSelected = ObservableBoolean(true)
  val stomaTypeSpinnerPosition = ObservableInt(-1).apply {
    compositeDisposable.add(
      onValueChanged { stomaTypeSelected.set(true) }
    )
  }

  val stomaSurgeryDateValues by lazy {
    StomaSurgeryDate.values()
      .map { applicationContext!!.getString(it.stringId) }
  }
  val stomaSurgeryDateSelected = ObservableBoolean(true)
  val stomaSurgeryDateSpinnerPosition = ObservableInt(-1).apply {
    compositeDisposable.add(
      onValueChanged { stomaSurgeryDateSelected.set(true) }
    )
  }

  lateinit var stoma_spinner: SpinnerView
  lateinit var stoma_date_spinner: SpinnerView

  override fun attachVariables(viewBinding: ControllerStomaInfoSu2Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStomaInfoSu2Binding) {
    stoma_spinner = viewBinding.stomaSpinner
    stoma_spinner.setMenuItems(stomaTypeValues)

    stoma_date_spinner = viewBinding.stomaDateSpinner
    stoma_date_spinner.setMenuItems(stomaSurgeryDateValues)
  }

  fun onNextClicked(){
    stomaTypeSelected.set(stomaTypeSpinnerPosition.get() >= 0)
    stomaSurgeryDateSelected.set(stomaSurgeryDateSpinnerPosition.get() >= 0)
    if(stomaTypeSelected.get() && stomaSurgeryDateSelected.get()){
      val stomaType = StomaType.fromOrdinal(stomaTypeSpinnerPosition.get())
      val stomaSurgeryDate = StomaSurgeryDate.fromOrdinal(stomaSurgeryDateSpinnerPosition.get())
      val data = SU2Model(stomaType, stomaSurgeryDate)
      getListener(FlowListener::class.java)!!.onNextClicked(data)
    }
  }

  override fun getLayout(): Int = R.layout.controller_stoma_info_su_2

  companion object{
    private const val TAG_CONTROLLER = "stoma_info_su_2_controller"

    fun getRouterTransaction() = RouterTransaction.with(StomaInfoSU2Controller())
      .tag(TAG_CONTROLLER)
  }

  override fun onItemIndexSelected(id: Int, index: Int) {
    if (id == stoma_spinner.id) {
      stomaTypeSelected.set(true)
      stomaTypeSpinnerPosition.set(index)
      stoma_spinner.textView.setText(stomaTypeValues.get(index))
    }
    else if (id == stoma_date_spinner.id) {
      stomaSurgeryDateSelected.set(true)
      stomaSurgeryDateSpinnerPosition.set(index)
      stoma_date_spinner.textView.setText(stomaSurgeryDateValues.get(index))
    }
  }
}