package com.coloplast.olympus.ui.feature.onboarding.createprofile.su2

import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStomaInfoSu2Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.SU2Model
import com.coloplast.olympus.ui.feature.onboarding.createprofile.types.StomaSurgeryDate
import com.coloplast.olympus.ui.feature.onboarding.createprofile.types.StomaType


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 01/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class StomaInfoSU2Controller: BaseController<ControllerStomaInfoSu2Binding>() {

  val stomaTypeValues by lazy {
    StomaType.values()
      .map { applicationContext!!.getString(it.stringId) }
  }
  val stomaTypeId = ObservableInt(-1)

  val stomaSurgeryDateValues by lazy {
    StomaSurgeryDate.values()
      .map { applicationContext!!.getString(it.stringId) }
  }
  val stomaSurgeryDateId = ObservableInt(-1)

  override fun attachVariables(viewBinding: ControllerStomaInfoSu2Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStomaInfoSu2Binding) {
    //noop
  }

  fun onNextClicked(){
    if(stomaTypeId.get() >= 0 && stomaSurgeryDateId.get() >= 0){
      val stomaType = StomaType.fromOrdinal(stomaTypeId.get())
      val stomaSurgeryDate = StomaSurgeryDate.fromOrdinal(stomaSurgeryDateId.get())
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
}