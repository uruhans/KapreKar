package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateInnerBlindBinding
import com.coloplast.olympus.ui.feature.guides.SingleGuideFlowController
import com.coloplast.olympus.ui.feature.guides.articles.models.GuideType
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class InnerBlindController : BaseHomeUIController<ControllerStateInnerBlindBinding>(), PatchVisible {

  override fun attachVariables(viewBinding: ControllerStateInnerBlindBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStateInnerBlindBinding) {
    // no-op
  }

  fun onApplicationGuideClicked() {
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
    private const val TAG_CONTROLLER = "state_inner_blind_controller"
    fun routerTransaction() = RouterTransaction.with(InnerBlindController()).tag(TAG_CONTROLLER)
  }
}