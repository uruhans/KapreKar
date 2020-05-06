package com.coloplast.olympus.ui.feature.transmitters.add

import android.os.Bundle
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.databinding.ControllerFlowBinding
import com.coloplast.olympus.ui.common.BaseFlowController

class AddTransmitterFlowController(args: Bundle) : BaseFlowController(args) {
  
  private val startFlowAt = args.getInt(BUNDLE_START_FLOW_AT)
  
  override fun startFlow(flowChildRouter: Router) {
    flowChildRouter.pushController(getInitialController())
  }
  
  private fun getInitialController() : RouterTransaction {
    TODO("Not yet implemented")
  }
  
  override fun attachVariables(viewBinding: ControllerFlowBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  companion object {
    private const val CONTROLLER_TAG = "add_transmitter_flow_controller"
    private const val BUNDLE_START_FLOW_AT = "BUNDLE_START_FLOW_AT";
    
    fun routerTransaction(startAt: Int = 0) : RouterTransaction {
      with(Bundle()) {
        putInt(BUNDLE_START_FLOW_AT, startAt)
        
        return RouterTransaction.with(AddTransmitterFlowController(this)).tag(CONTROLLER_TAG)
      }
    }
  }
}