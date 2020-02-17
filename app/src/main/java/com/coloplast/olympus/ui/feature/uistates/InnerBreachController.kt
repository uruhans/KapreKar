package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateInnerBreachBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class InnerLeakController : BaseHomeUIController<ControllerStateInnerBreachBinding>(), PatchVisible {
  
  override fun onViewBound(viewBinding: ControllerStateInnerBreachBinding) {
    // no-op
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_inner_breach
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_inner_leak_controller"
    fun routerTransaction() = RouterTransaction.with(InnerLeakController()).tag(TAG_CONTROLLER)
  }
}