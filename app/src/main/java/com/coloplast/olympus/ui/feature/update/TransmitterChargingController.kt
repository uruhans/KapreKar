package com.coloplast.olympus.ui.feature.update


import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTransmitterChargingBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener

class TransmitterChargingController : BaseController<ControllerTransmitterChargingBinding>(), TopMenuVisibilityListener {
  
  override fun attachVariables(viewBinding: ControllerTransmitterChargingBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerTransmitterChargingBinding) {
    // abstract function that must be implemented
  }

  override fun showBackButton(): Boolean = true

  override fun showCloseButton(): Boolean = false

  fun onContinueClicked() = getListener(FlowListener::class.java)?.onNextClicked(true)

  fun onDiscard() = getListener(FlowListener::class.java)?.onPreviousClicked()

  override fun getLayout(): Int = R.layout.controller_transmitter_charging

  companion object {
    private const val TAG_CONTROLLER = "update_transmitter_charging_controller"
    fun routerTransaction() = RouterTransaction.with(TransmitterChargingController()).tag(TAG_CONTROLLER)
  }
}