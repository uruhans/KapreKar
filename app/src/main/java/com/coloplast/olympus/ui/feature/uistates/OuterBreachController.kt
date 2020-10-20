package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateOuterBreachBinding
import com.coloplast.olympus.ui.feature.guides.SingleGuideFlowController
import com.coloplast.olympus.ui.feature.guides.articles.models.GuideType
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class OuterBreachController : BaseHomeUIController<ControllerStateOuterBreachBinding>(), PatchVisible {

  override fun attachVariables(viewBinding: ControllerStateOuterBreachBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStateOuterBreachBinding) {
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
    return R.layout.controller_state_outer_breach
  }

  companion object {
    private const val TAG_CONTROLLER = "state_outer_breach_controller"
    fun routerTransaction() = RouterTransaction.with(OuterBreachController()).tag(TAG_CONTROLLER)
  }
}