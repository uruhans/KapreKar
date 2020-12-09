package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateNoBatteryBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class NoBatteryController : BaseHomeUIControllerAppTour<ControllerStateNoBatteryBinding>(),
  PatchVisible {
  
  override fun onViewBound(viewBinding: ControllerStateNoBatteryBinding) {
    // no-op
  }

  override fun attachVariables(viewBinding: ControllerStateNoBatteryBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  fun onAppTourClicked() {
    startAppTour()
  }

  override fun patchInError(): Boolean {
    return true
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_no_battery
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_no_battery_controller"
    fun routerTransaction() = RouterTransaction.with(NoBatteryController()).tag(TAG_CONTROLLER)
  }
}