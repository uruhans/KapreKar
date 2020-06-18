package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.ServiceDispatcher
import com.coloplast.olympus.bluetooth.model.ServiceEvent
import com.coloplast.olympus.databinding.ControllerStateTransmitterNotFoundBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class TransmitterNotFoundController : BaseHomeUIController<ControllerStateTransmitterNotFoundBinding>(), PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateTransmitterNotFoundBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateTransmitterNotFoundBinding) {
    // no-op
  }
  
  fun onClicked() {
    ServiceDispatcher.dispatchEvent(ServiceEvent.RetryConnection())
  }
  
  override fun patchInError(): Boolean {
    return true
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_transmitter_not_found
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_transmitter_not_found_controller"
    fun routerTransaction() = RouterTransaction.with(TransmitterNotFoundController()).tag(TAG_CONTROLLER)
  }
}