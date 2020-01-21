package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateScanningBinding

class ScanningController : BaseHomeUIController<ControllerStateScanningBinding>() {
  
  override fun onViewBound(viewBinding: ControllerStateScanningBinding) {
    // no-op
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_scanning
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_scanning_controller"
    fun routerTransaction() = RouterTransaction.with(ScanningController()).tag(TAG_CONTROLLER)
  }
}