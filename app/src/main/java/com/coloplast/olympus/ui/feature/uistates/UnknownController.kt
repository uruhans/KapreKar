package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateUnknownBinding

class UnknownController : BaseHomeUIController<ControllerStateUnknownBinding>() {
  
  override fun onViewBound(viewBinding: ControllerStateUnknownBinding) {
    // no-op
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_unknown
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_unknown_controller"
    fun routerTransaction() = RouterTransaction.with(UnknownController()).tag(TAG_CONTROLLER)
  }
}