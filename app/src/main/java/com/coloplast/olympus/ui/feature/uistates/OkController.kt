package com.coloplast.olympus.ui.feature.uistates

import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateOkBinding
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class OkController : BaseHomeUIController<ControllerStateOkBinding>(), PatchVisible {

  val shouldShowAppTourButton = ObservableBoolean(false)

  override fun onViewBound(viewBinding: ControllerStateOkBinding) {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.let { it1 -> shouldShowAppTourButton.set(it1.shouldShowAppTour()) }
    }
  }

  fun startAppTour() {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.startAppTour()
    }
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_ok
  }

  override fun attachVariables(viewBinding: ControllerStateOkBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  companion object {
    private const val TAG_CONTROLLER = "state_ok_controller"

    fun routerTransaction() = RouterTransaction.with(OkController()).tag(TAG_CONTROLLER)
  }
}