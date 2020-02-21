package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateTransmitterDetachedBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class TransmitterDetachedController : BaseHomeUIController<ControllerStateTransmitterDetachedBinding>(), PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateTransmitterDetachedBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateTransmitterDetachedBinding) {
    // no-op
  }
  
  fun onApplicationGuideClicked() {
  
  }
  
  override fun patchInError(): Boolean {
    return true
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_transmitter_detached
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_transmitter_detached_controller"
    fun routerTransaction() = RouterTransaction.with(TransmitterDetachedController()).tag(TAG_CONTROLLER)
  }
}