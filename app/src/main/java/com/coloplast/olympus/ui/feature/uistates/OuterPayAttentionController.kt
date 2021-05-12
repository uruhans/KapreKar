package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStatePayAttentionBinding
import com.coloplast.olympus.ui.feature.guides.flows.applying.GuideApplyingTransmitterFlowController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class PayAttentionController : BaseHomeUIController<ControllerStatePayAttentionBinding>(), PatchVisible {

  override fun attachVariables(viewBinding: ControllerStatePayAttentionBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStatePayAttentionBinding) {
    // no-op
  }

  fun onApplicationGuideClicked() {
    getContentRouter().pushController(
      GuideApplyingTransmitterFlowController.routerTransaction()
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler()))
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_pay_attention
  }

  companion object {
    private const val TAG_CONTROLLER = "state_pay_attention_controller"
    fun routerTransaction() = RouterTransaction.with(PayAttentionController()).tag(TAG_CONTROLLER)
  }
}