package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateDirtInTransmitterBinding
import com.coloplast.olympus.ui.feature.guides.flows.applying.GuideApplyingTransmitterFlowController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class DirtInTransmitterController : BaseHomeUIController<ControllerStateDirtInTransmitterBinding>(), PatchVisible {

  override fun attachVariables(viewBinding: ControllerStateDirtInTransmitterBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStateDirtInTransmitterBinding) {
    // no-op
  }

  fun onGuideClicked() {
    getContentRouter().pushController(
      GuideApplyingTransmitterFlowController.routerTransaction()
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler()))
  }

  override fun patchInError(): Boolean {
    return true
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_dirt_in_transmitter
  }

  companion object {
    private const val TAG_CONTROLLER = "state_dirt_in_transmitter_controller"
    fun routerTransaction() = RouterTransaction.with(DirtInTransmitterController()).tag(TAG_CONTROLLER)
  }
} 