package com.coloplast.olympus.ui.feature.transmitters

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTransmitterGuideAt1Binding
import com.coloplast.olympus.ui.common.CustomToolbarController

class GuideTransmitterAT1Controller(args: Bundle? = null) : CustomToolbarController<ControllerTransmitterGuideAt1Binding>(args) {
  
  override fun attachVariables(viewBinding: ControllerTransmitterGuideAt1Binding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun getNavigationIconResId() =  R.drawable.ic_arrow
  
  
  override fun getLayout() = R.layout.controller_transmitter_guide_at_1
  
  companion object {
    private const val TAG_CONTROLLER = "guide_transmitters_at1_controller"
    
    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(GuideTransmitterAT1Controller())
        .tag(TAG_CONTROLLER)
    }
  }
}