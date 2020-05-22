package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateMultipleTransmittersBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class MultipleTransmittersController : BaseHomeUIController<ControllerStateMultipleTransmittersBinding>(), PatchVisible {

  override fun onViewBound(viewBinding: ControllerStateMultipleTransmittersBinding) {
    // no-op
  }

  override fun patchVisible(): Boolean {
    return false
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_multiple_transmitters
  }

  companion object {
    private const val TAG_CONTROLLER = "state_multiple_transmitters_controller"
    fun routerTransaction() = RouterTransaction.with(MultipleTransmittersController()).tag(TAG_CONTROLLER)
  }
}