package com.coloplast.olympus.ui.feature.transmitters.add

import android.os.Bundle
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.FlowFactoryController
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterModel
import org.koin.core.inject

class AddTransmitterFlowController(args: Bundle) : FlowFactoryController<AddTransmitterModel>(args) {
  
  private val initialValue = args.getInt(BUNDLE_START_FLOW_AT, -1)
  private val addTransmitterFactory: FlowFactory<AddTransmitterModel> by inject()
  
  override fun startFlow(flowChildRouter: Router) {
    addTransmitterFactory.overrideStartingPosition(initialValue)
    super.startFlow(flowChildRouter)
  }
  
  override fun getFlowFactory(): FlowFactory<AddTransmitterModel> {
    return addTransmitterFactory
  }
  
  companion object {
    private const val CONTROLLER_TAG = "add_transmitter_flow_controller"
    private const val BUNDLE_START_FLOW_AT = "BUNDLE_START_FLOW_AT"
    
    fun routerTransaction(startAt: Int = -1): RouterTransaction {
      with(Bundle()) {
        putInt(BUNDLE_START_FLOW_AT, startAt)
        
        return RouterTransaction.with(AddTransmitterFlowController(this))
          .tag(CONTROLLER_TAG)
      }
    }
  }
}