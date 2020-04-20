package com.coloplast.olympus.ui.feature.transmitters

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTransmittersNotAddedBinding
import com.coloplast.olympus.ui.common.CustomToolbarController

class NotAddedTransmittersController : CustomToolbarController<ControllerTransmittersNotAddedBinding>() {
  
  override fun attachVariables(viewBinding: ControllerTransmittersNotAddedBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  fun onAddTransmitterClicked(){
    router.pushController(
      TransmittersController.routerTransaction()
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }
  
  override fun getNavigationIconResId(): Int {
    return R.drawable.ic_arrow
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_transmitters_not_added
  }
  
  companion object {
    private const val TAG_CONTROLLER = "not_added_transmitters_controller"
    
    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(NotAddedTransmittersController())
        .tag(TAG_CONTROLLER)
    }
  }
}
