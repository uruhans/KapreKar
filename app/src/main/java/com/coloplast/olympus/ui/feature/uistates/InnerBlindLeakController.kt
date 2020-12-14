package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateInnerBlindLeakBinding
import com.coloplast.olympus.ui.feature.guides.SingleGuideFlowController
import com.coloplast.olympus.ui.feature.guides.articles.models.GuideType
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class InnerBlindLeakController : BaseHomeUIController<ControllerStateInnerBlindLeakBinding>(), PatchVisible {

  override fun attachVariables(viewBinding: ControllerStateInnerBlindLeakBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStateInnerBlindLeakBinding) {
    // no-op
  }

  fun onGuideClicked() {
    getContentRouter().pushController(
      SingleGuideFlowController.routerTransaction(GuideType.APPLICATION)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_inner_blind
  }

  companion object {
    private const val TAG_CONTROLLER = "state_inner_blind_leak_controller"
    fun routerTransaction() = RouterTransaction.with(InnerBlindLeakController()).tag(TAG_CONTROLLER)
  }
}