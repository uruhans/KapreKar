package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateFirmwareAvailableBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class FirmwareAvailableController : BaseHomeUIController<ControllerStateFirmwareAvailableBinding>(), PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateFirmwareAvailableBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateFirmwareAvailableBinding) {
    // no-op
  }
  
  fun onUpdateFirmwareClicked() {

  }
  
  override fun patchInError(): Boolean {
    return true
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_firmware_available
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_firmware_available_controller"
    fun routerTransaction() = RouterTransaction.with(FirmwareAvailableController()).tag(TAG_CONTROLLER)
  }
}