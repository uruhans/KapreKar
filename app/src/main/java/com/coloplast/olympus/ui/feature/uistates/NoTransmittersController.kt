package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateNoTransmittersBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterFlowController

class NoTransmittersController : BaseHomeUIController<ControllerStateNoTransmittersBinding>(), PatchVisible {

  override fun onViewBound(viewBinding: ControllerStateNoTransmittersBinding) {
    // no-op
  }

  fun onAddTransmitterClicked() {
    getContentRouter().pushController(
      AddTransmitterFlowController.routerTransaction(2)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  override fun patchInError(): Boolean {
    return true
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_no_transmitters
  }

  companion object {
    private const val TAG_CONTROLLER = "state_no_transmitters_controller"
    fun routerTransaction() = RouterTransaction.with(NoTransmittersController()).tag(TAG_CONTROLLER)
  }
}