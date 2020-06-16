package com.coloplast.olympus.ui.feature.settings

import android.os.Bundle
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.BaseFlowController
import com.coloplast.olympus.ui.common.DrawerStateDispatcher


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SettingsFlowController(args: Bundle? = null): BaseFlowController(args),
  SettingsController.Listener {

  init {
    showToolbar.set(true)
  }


  override fun startFlow(flowChildRouter: Router) {
    flowChildRouter.setRoot(getInitialController())
  }

  private fun getInitialController(): RouterTransaction = SettingsController.routerTransaction().apply{
    controller().targetController = this@SettingsFlowController
  }

  override fun onCloseClicked() {
    DrawerStateDispatcher.closeNavigationDrawerOnHome()
    super.onCloseClicked()
  }

  override fun setBedtimeAlarm() {
    pushController(BedtimeAlarmController.routerTransaction())
  }

  override fun chooseSensors() {
    pushController(ChooseSensorsController.routerTransaction())
  }

  companion object{

    private const val TAG_CONTROLLER = "settings_flow_controller"

    fun routerTransaction(): RouterTransaction{
      return RouterTransaction.with(SettingsFlowController()).tag(TAG_CONTROLLER)
    }
  }
}