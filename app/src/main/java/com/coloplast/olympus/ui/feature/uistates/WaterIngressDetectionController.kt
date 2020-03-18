package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateWaterIngressDetectionBinding
import com.coloplast.olympus.ui.feature.guides.cleaning.CleaningGuideController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class WaterIngressDetectionController : BaseHomeUIController<ControllerStateWaterIngressDetectionBinding>(), PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateWaterIngressDetectionBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateWaterIngressDetectionBinding) {
    // no-op
  }
  
  fun onApplicationGuideClicked() {
    router.pushController(
      CleaningGuideController.routerTransaction()
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }
  
  override fun patchInError(): Boolean {
    return true
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_water_ingress_detection
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_water_ingress_detection_controller"
    fun routerTransaction() = RouterTransaction.with(WaterIngressDetectionController()).tag(TAG_CONTROLLER)
  }
}