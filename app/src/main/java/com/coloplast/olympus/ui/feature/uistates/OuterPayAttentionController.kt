package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateOuterPayAttentionBinding
import com.coloplast.olympus.ui.feature.guides.SingleGuideFlowController
import com.coloplast.olympus.ui.feature.guides.articles.models.GuideType
import com.coloplast.olympus.ui.feature.guides.flows.applying.GuideApplyingTransmitterFlowController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class OuterPayAttentionController : BaseHomeUIController<ControllerStateOuterPayAttentionBinding>(), PatchVisible {

  override fun attachVariables(viewBinding: ControllerStateOuterPayAttentionBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStateOuterPayAttentionBinding) {
    // no-op
  }

  fun onGuideClicked() {
    getContentRouter().pushController(
      SingleGuideFlowController.routerTransaction(GuideType.UNDERSTANDING_SENSORS)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_outer_pay_attention
  }

  companion object {
    private const val TAG_CONTROLLER = "state_outer_pay_attention_controller"
    fun routerTransaction() = RouterTransaction.with(OuterPayAttentionController()).tag(TAG_CONTROLLER)
  }
}