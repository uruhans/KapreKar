package com.coloplast.olympus.ui.feature.settings

import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerBedtimeAlarmBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.settings.models.Sensors


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 04/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class BedtimeAlarmController: BaseController<ControllerBedtimeAlarmBinding>() {

  val allSensorsSelected = ObservableBoolean(true)

  override fun attachVariables(viewBinding: ControllerBedtimeAlarmBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerBedtimeAlarmBinding) {
    //noop
  }

  fun onOptionClicked(sensors: Sensors){
    when(sensors){
      Sensors.ALL -> allSensorsSelected.set(true)
      Sensors.OUTER -> allSensorsSelected.set(false)
    }
  }

  override fun getLayout(): Int = R.layout.controller_bedtime_alarm

  companion object{

    private const val TAG_CONTROLLER = "bedtime_alarm_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(BedtimeAlarmController()).tag(TAG_CONTROLLER)
    }
  }
}