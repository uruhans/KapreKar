package com.coloplast.olympus.ui.feature.uistates

import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateMultipleTransmittersBinding
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class MultipleTransmittersController : BaseHomeUIController<ControllerStateMultipleTransmittersBinding>(), PatchVisible {

  val shouldShowAppTourButton = ObservableBoolean(false)

  override fun onViewBound(viewBinding: ControllerStateMultipleTransmittersBinding) {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.let { it1 -> shouldShowAppTourButton.set(it1.shouldShowAppTour()) }
    }
  }

  override fun attachVariables(viewBinding: ControllerStateMultipleTransmittersBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  fun startAppTour() {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.startAppTour()
    }
  }

  override fun patchVisible(): Boolean {
    return true
  }

  override fun patchInError(): Boolean {
    return true
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_multiple_transmitters
  }

  companion object {
    private const val TAG_CONTROLLER = "state_multiple_transmitters_controller"
    fun routerTransaction() = RouterTransaction.with(MultipleTransmittersController()).tag(TAG_CONTROLLER)
  }
}