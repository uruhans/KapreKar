package com.coloplast.olympus.ui.feature.settings

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSettingsBinding
import com.coloplast.olympus.ui.common.BaseController

class SettingsController: BaseController<ControllerSettingsBinding>() {

  interface Listener{
    fun setBedtimeAlarm()
  }

  override fun attachVariables(viewBinding: ControllerSettingsBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSettingsBinding) {
    //noop
  }

  fun setBedtimeAlarmClicked(){
    getListener(Listener::class.java)!!.setBedtimeAlarm()
  }

  override fun getLayout(): Int = R.layout.controller_settings

  companion object{

    private const val TAG_CONTROLLER = "settings_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SettingsController()).tag(TAG_CONTROLLER)
    }
  }
}
