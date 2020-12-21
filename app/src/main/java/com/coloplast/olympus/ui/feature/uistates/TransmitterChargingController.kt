package com.coloplast.olympus.ui.feature.uistates

import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateTransmitterChargingBinding
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class TransmitterChargingController : BaseHomeUIController<ControllerStateTransmitterChargingBinding>(), PatchVisible {

  val shouldShowAppTourButton = ObservableBoolean(false)

  override fun onViewBound(viewBinding: ControllerStateTransmitterChargingBinding) {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.let { it1 -> shouldShowAppTourButton.set(it1.shouldShowAppTour()) }
    }
  }

  override fun attachVariables(viewBinding: ControllerStateTransmitterChargingBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  fun startAppTour() {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.startAppTour()
    }
  }

   override fun patchVisible(): Boolean {
    return false
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_transmitter_charging
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_transmitter_charging_controller"
    fun routerTransaction() = RouterTransaction.with(TransmitterChargingController()).tag(TAG_CONTROLLER)
  }
}