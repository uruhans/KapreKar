package com.coloplast.olympus.ui.feature.transmitters.at1

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTransmitterGuideAt1Binding
import com.coloplast.olympus.ui.common.CustomToolbarController

class AddTransmitterAT1Controller(args: Bundle? = null) : CustomToolbarController<ControllerTransmitterGuideAt1Binding>(args) {
  
  override fun attachVariables(viewBinding: ControllerTransmitterGuideAt1Binding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun getNavigationIconResId(): Int = R.drawable.ic_arrow
  
  override fun getLayout(): Int = R.layout.controller_transmitter_guide_at_1
  
  companion object {
    private const val TAG_CONTROLLER = "add_transmitter_at1_controller"
    
    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(AddTransmitterAT1Controller())
        .tag(TAG_CONTROLLER)
    }
  }
}

