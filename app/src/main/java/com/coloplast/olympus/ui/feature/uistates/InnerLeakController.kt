package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateInnerLeakBinding

class InnerLeakController : BaseHomeUIController<ControllerStateInnerLeakBinding>() {
  
  override fun onViewBound(viewBinding: ControllerStateInnerLeakBinding) {
    // no-op
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_inner_leak
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_inner_leak_controller"
    fun routerTransaction() = RouterTransaction.with(InnerLeakController()).tag(TAG_CONTROLLER)
  }
}