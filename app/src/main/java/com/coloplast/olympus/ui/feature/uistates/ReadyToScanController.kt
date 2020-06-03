package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateReadyToScanBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class ReadyToScanController : BaseHomeUIController<ControllerStateReadyToScanBinding>(), PatchVisible {
  
  override fun onViewBound(viewBinding: ControllerStateReadyToScanBinding) {
    // no-op
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_ready_to_scan
  }

  override fun patchVisible(): Boolean {
    return false
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_ready_to_scan_controller"
    fun routerTransaction() = RouterTransaction.with(ReadyToScanController()).tag(TAG_CONTROLLER)
  }
}