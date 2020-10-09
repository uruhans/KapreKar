package com.coloplast.olympus.ui.feature.update.initiate


import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerFirmwareInitiateBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener

class FirmwareInitiateController : BaseController<ControllerFirmwareInitiateBinding>(), TopMenuVisibilityListener {
  
  override fun attachVariables(viewBinding: ControllerFirmwareInitiateBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerFirmwareInitiateBinding) {
    // abstract function that must be implemented
  }

  override fun showBackButton(): Boolean = true

  override fun showCloseButton(): Boolean = false

  fun onContinueClicked() = getListener(FlowListener::class.java)?.onNextClicked()

  fun onDiscard() = getListener(FlowListener::class.java)?.onPreviousClicked()

  override fun getLayout(): Int = R.layout.controller_firmware_initiate

  companion object {
    private const val TAG_CONTROLLER = "initiate_firmware_controller"
    fun routerTransaction() = RouterTransaction.with(FirmwareInitiateController()).tag(
      TAG_CONTROLLER
    )
  }
}