package com.coloplast.olympus.ui.feature.transmitters.add.ble3

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAddTransmitterBle3Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener

class AddTransmitterBle3Controller : BaseController<ControllerAddTransmitterBle3Binding>() {

  override fun attachVariables(viewBinding: ControllerAddTransmitterBle3Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerAddTransmitterBle3Binding) {
    /* no-op */
  }

  fun onNextClicked() {
    getListener(FlowListener::class.java)?.onNextClicked()
  }

  override fun getLayout(): Int {
    return R.layout.controller_add_transmitter_ble3
  }

  companion object {
    private const val CONTROLLER_TAG = "add_transmitter_ble3_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(AddTransmitterBle3Controller()).tag(CONTROLLER_TAG)
    }
  }


}