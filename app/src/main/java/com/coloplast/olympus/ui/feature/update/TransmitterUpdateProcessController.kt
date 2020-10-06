package com.coloplast.olympus.ui.feature.update


import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTransmitterProcessUpdateBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener

class TransmitterUpdateProcessController : BaseController<ControllerTransmitterProcessUpdateBinding>(), TopMenuVisibilityListener {

  val resourceId = R.drawable.placeholder // TODO change to proper video or image

  override fun attachVariables(viewBinding: ControllerTransmitterProcessUpdateBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerTransmitterProcessUpdateBinding) {
    // abstract function that must be implemented
  }

  override fun showBackButton(): Boolean = true

  override fun showCloseButton(): Boolean = false

  fun onContinueClicked() = getListener(FlowListener::class.java)?.onNextClicked(true)

  override fun getLayout(): Int = R.layout.controller_transmitter_process_update

  companion object {
    private const val TAG_CONTROLLER = "update_transmitter_update_process"
    fun routerTransaction() = RouterTransaction.with(TransmitterUpdateProcessController()).tag(TAG_CONTROLLER)
  }
}