package com.coloplast.olympus.ui.feature.settings

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerChooseSensorsBinding
import com.coloplast.olympus.ui.common.BaseController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ChooseSensorsController: BaseController<ControllerChooseSensorsBinding>() {
  override fun attachVariables(viewBinding: ControllerChooseSensorsBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerChooseSensorsBinding) {
    //noop
  }

  override fun getLayout(): Int = R.layout.controller_choose_sensors

  companion object{
    private const val TAG_CONTROLLER = "choose_sensors_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(ChooseSensorsController()).tag(TAG_CONTROLLER)
    }
  }
}