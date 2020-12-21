package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateTransmitterAddBinding
import com.coloplast.olympus.ui.feature.guides.SingleGuideFlowController
import com.coloplast.olympus.ui.feature.guides.articles.models.GuideType
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class NoTransmittersController : BaseHomeUIController<ControllerStateTransmitterAddBinding>(), PatchVisible {

  override fun attachVariables(viewBinding: ControllerStateTransmitterAddBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStateTransmitterAddBinding) {
    // no-op
  }

  fun onAddTransmitterClicked() {
    getContentRouter().pushController(
      SingleGuideFlowController.routerTransaction(GuideType.GET_STARTED)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  override fun patchInError(): Boolean {
    return true
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_transmitter_add
  }

  companion object {
    private const val TAG_CONTROLLER = "state_transmitter_detached_controller"
    fun routerTransaction() = RouterTransaction.with(NoTransmittersController()).tag(TAG_CONTROLLER)
  }
}