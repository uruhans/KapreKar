package com.coloplast.olympus.ui.feature.transmitters.add

import android.os.Bundle
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.FlowFactoryController
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterModel
import org.koin.core.inject

class AddTransmitterFlowController(args: Bundle) : FlowFactoryController<AddTransmitterModel>(args), MediaGuideController.Listener {
  
  private val startAtIndex = args.getInt(BUNDLE_START_FLOW_AT, 0)
  private val addTransmitterFactory: FlowFactory<AddTransmitterModel> by inject()
  
  override fun startFlow(flowChildRouter: Router) {
    addTransmitterFactory.overrideStartingPosition(startAtIndex)
    super.startFlow(flowChildRouter)
  }
  
  override fun getFlowFactory(): FlowFactory<AddTransmitterModel> {
    return addTransmitterFactory
  }

  override fun onQuestionClicked(screen: String) {
    //TODO: Guidelines on Questions have not been defined.
  }
  
  companion object {
    private const val CONTROLLER_TAG = "add_transmitter_flow_controller"
    private const val BUNDLE_START_FLOW_AT = "BUNDLE_START_FLOW_AT"
    
    fun routerTransaction(startAtIndex: Int = 0): RouterTransaction {
      with(Bundle()) {
        putInt(BUNDLE_START_FLOW_AT, startAtIndex)
        
        return RouterTransaction.with(AddTransmitterFlowController(this))
          .tag(CONTROLLER_TAG)
      }
    }
  }
}