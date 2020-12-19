package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateTransmitterChargingBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class TransmitterChargingController : BaseHomeUIControllerAppTour<ControllerStateTransmitterChargingBinding>(), PatchVisible {
  
  override fun onViewBound(viewBinding: ControllerStateTransmitterChargingBinding) {
    // no-op
  }

  override fun attachVariables(viewBinding: ControllerStateTransmitterChargingBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

   override fun patchVisible(): Boolean {
    return false
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_transmitter_charging
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_transmitter_charging_controller"
    fun routerTransaction() = RouterTransaction.with(TransmitterChargingController()).tag(TAG_CONTROLLER)
  }
}