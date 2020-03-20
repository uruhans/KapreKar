package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateTotalBlindBinding
import com.coloplast.olympus.databinding.ControllerStateTransmitterDetachedBinding
import com.coloplast.olympus.ui.feature.guides.application.ApplicationGuideController
import com.coloplast.olympus.ui.feature.guides.getstarted.GetStartedGuideController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class TotalBlindController : BaseHomeUIController<ControllerStateTotalBlindBinding>(), PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateTotalBlindBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateTotalBlindBinding) {
    // no-op
  }
  
  fun onApplicationGuideClicked() {
    getContentRouter().pushController(
      ApplicationGuideController.routerTransaction()
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }
  
  override fun patchInError(): Boolean {
    return true
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_total_blind
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_total_blind_controller"
    fun routerTransaction() = RouterTransaction.with(TotalBlindController()).tag(TAG_CONTROLLER)
  }
}