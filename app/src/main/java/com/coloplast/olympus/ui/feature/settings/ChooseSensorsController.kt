package com.coloplast.olympus.ui.feature.settings

import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerChooseSensorsBinding
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.shared.types.Sensors
import com.coloplast.olympus.ui.common.BaseController
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ChooseSensorsController: BaseController<ControllerChooseSensorsBinding>() {

  private val preferences: PreferencesImpl by inject()
  val allSensorsSelected = ObservableBoolean(preferences.ringingAlertSensor == Sensors.ALL)

  override fun attachVariables(viewBinding: ControllerChooseSensorsBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerChooseSensorsBinding) {
    //noop
  }

  fun onSaveChanges(){
    preferences.ringingAlertSensor = when(allSensorsSelected.get()) {
      true -> Sensors.ALL
      false -> Sensors.OUTER
    }
    parentController?.handleBack()
  }

  fun onDiscard() = parentController?.handleBack()

  override fun getLayout(): Int = R.layout.controller_choose_sensors

  companion object{
    private const val TAG_CONTROLLER = "choose_sensors_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(ChooseSensorsController()).tag(TAG_CONTROLLER)
    }
  }
}