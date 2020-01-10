package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateOkBinding

class OkController : BaseHomeUIController<ControllerStateOkBinding>() {
  
  override fun onViewBound(viewBinding: ControllerStateOkBinding) {
    // no-op
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_ok
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_ok_controller"
    fun routerTransaction() = RouterTransaction.with(OkController()).tag(TAG_CONTROLLER)
  }
}