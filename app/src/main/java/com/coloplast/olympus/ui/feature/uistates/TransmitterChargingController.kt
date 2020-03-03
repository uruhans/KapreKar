package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateTransmitterChargingBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class TransmitterChargingController : BaseHomeUIController<ControllerStateTransmitterChargingBinding>(), PatchVisible {
  
  override fun onViewBound(viewBinding: ControllerStateTransmitterChargingBinding) {
    // no-op
  }
  
  fun onApplicationGuideClicked() {
  
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