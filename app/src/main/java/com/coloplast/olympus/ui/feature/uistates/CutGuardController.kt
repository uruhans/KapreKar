package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateCutguardBinding
import com.coloplast.olympus.ui.feature.guides.GuideController
import com.coloplast.olympus.ui.feature.guides.models.GuideType
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class CutGuardController : BaseHomeUIController<ControllerStateCutguardBinding>(), PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateCutguardBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateCutguardBinding) {
    // no-op
  }
  
  fun onApplicationGuideClicked() {
    getContentRouter().pushController(
      GuideController.routerTransaction(GuideType.APPLICATION)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }
  
  override fun patchInError(): Boolean {
    return true
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_cutguard
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_cut_guard_controller"
    fun routerTransaction() = RouterTransaction.with(CutGuardController()).tag(TAG_CONTROLLER)
  }
}