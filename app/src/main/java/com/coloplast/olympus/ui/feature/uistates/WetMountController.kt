package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateWetMountBinding
import com.coloplast.olympus.ui.feature.guides.SingleGuideFlowController
import com.coloplast.olympus.ui.feature.guides.articles.models.GuideType
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class WetMountController : BaseHomeUIController<ControllerStateWetMountBinding>(), PatchVisible {

  override fun attachVariables(viewBinding: ControllerStateWetMountBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStateWetMountBinding) {
    // no-op
  }

  fun onGuideClicked() {
    getContentRouter().pushController(
      SingleGuideFlowController.routerTransaction(GuideType.APPLICATION_TIPS)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  override fun patchInError(): Boolean {
    return true
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_wet_mount
  }

  companion object {
    private const val TAG_CONTROLLER = "state_wet_mount_controller"
    fun routerTransaction() = RouterTransaction.with(WetMountController()).tag(TAG_CONTROLLER)
  }
}