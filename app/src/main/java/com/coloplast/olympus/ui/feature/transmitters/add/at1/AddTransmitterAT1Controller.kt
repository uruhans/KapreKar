package com.coloplast.olympus.ui.feature.transmitters.add.at1

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAddTransmitterAt1Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener

class AddTransmitterAT1Controller(args: Bundle? = null) : BaseController<ControllerAddTransmitterAt1Binding>(args) {
  
  override fun attachVariables(viewBinding: ControllerAddTransmitterAt1Binding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerAddTransmitterAt1Binding) {
    /* no-op */
  }
  
  fun onNextClicked() {
    getListener(FlowListener::class.java)?.onNextClicked()
  }
  
  override fun getLayout(): Int = R.layout.controller_add_transmitter_at_1
  
  companion object {
    private const val TAG_CONTROLLER = "add_transmitter_at1_controller"
    
    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(AddTransmitterAT1Controller())
        .tag(TAG_CONTROLLER)
    }
  }
}

