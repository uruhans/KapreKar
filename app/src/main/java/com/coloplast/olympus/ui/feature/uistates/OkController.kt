package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateOkBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class OkController : BaseHomeUIControllerAppTour<ControllerStateOkBinding>(), PatchVisible {

  override fun onViewBound(viewBinding: ControllerStateOkBinding) {
    // no-op
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_ok
  }

  override fun attachVariables(viewBinding: ControllerStateOkBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  companion object {
    private const val TAG_CONTROLLER = "state_ok_controller"
    fun routerTransaction() = RouterTransaction.with(OkController()).tag(TAG_CONTROLLER)
  }
}