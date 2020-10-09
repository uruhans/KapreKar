package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateFirmwareAvailableBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible
import com.coloplast.olympus.ui.feature.update.progress.FirmwareProgressController

class FirmwareAvailableController : BaseHomeUIController<ControllerStateFirmwareAvailableBinding>(), PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateFirmwareAvailableBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateFirmwareAvailableBinding) {
    // no-op
  }
  
  fun onUpdateFirmwareClicked() {
    // this needs to open the proper flowController
//    getContentRouter().pushController(
//      FirmwareProgressController.routerTransaction("E3:34:08:30:D7:65")
//      .pushChangeHandler(VerticalChangeHandler())
//      .popChangeHandler(VerticalChangeHandler())
//    )
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