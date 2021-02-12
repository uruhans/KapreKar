package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateMultipleTransmittersBinding
import com.coloplast.olympus.ui.feature.home.models.LastCheckVisible
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class MultipleTransmittersController : BaseHomeUIControllerAppTour<ControllerStateMultipleTransmittersBinding>(), PatchVisible, LastCheckVisible {

  override fun attachVariables(viewBinding: ControllerStateMultipleTransmittersBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun patchVisible(): Boolean {
    return true
  }

  override fun patchInError(): Boolean {
    return true
  }

  override fun lastCheckVisible(): Boolean {
    return true
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_multiple_transmitters
  }

  companion object {
    private const val TAG_CONTROLLER = "state_multiple_transmitters_controller"
    fun routerTransaction() = RouterTransaction.with(MultipleTransmittersController()).tag(TAG_CONTROLLER)
  }
}