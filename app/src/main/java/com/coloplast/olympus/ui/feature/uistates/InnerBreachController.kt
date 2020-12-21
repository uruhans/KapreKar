package com.coloplast.olympus.ui.feature.uistates

import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateInnerBreachBinding
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class InnerBreachController : BaseHomeUIController<ControllerStateInnerBreachBinding>(), PatchVisible {

  val shouldShowAppTourButton = ObservableBoolean(false)

  override fun onViewBound(viewBinding: ControllerStateInnerBreachBinding) {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.let { it1 -> shouldShowAppTourButton.set(it1.shouldShowAppTour()) }
    }
  }

  fun startAppTour() {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.startAppTour()
    }
  }

  override fun attachVariables(viewBinding: ControllerStateInnerBreachBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_inner_breach
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_inner_breach_controller"
    fun routerTransaction() = RouterTransaction.with(InnerBreachController()).tag(TAG_CONTROLLER)
  }
}