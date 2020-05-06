package com.coloplast.olympus.ui.feature.transmitters.add.at9

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAddTransmitterAt9Binding
import com.coloplast.olympus.ui.common.BaseController

class AddTransmitterAT9Controller(args: Bundle) : BaseController<ControllerAddTransmitterAt9Binding>(args) {
  
  private val transmitterToSearchFor = args.getString(BUNDLE_TRANSMITTER_NAME_TO_SEARCH)
  
  override fun attachVariables(viewBinding: ControllerAddTransmitterAt9Binding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerAddTransmitterAt9Binding) {
  
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_add_transmitter_at_9
  }
  
  companion object {
    private const val CONTROLLER_TAG = "add_transmitter_at9_controller"
    private const val BUNDLE_TRANSMITTER_NAME_TO_SEARCH = "BUNDLE_TRANSMITTER_NAME_TO_SEARCH"
    
    fun routerTransaction(transmitterName: String) : RouterTransaction {
      with(Bundle()) {
        putString(BUNDLE_TRANSMITTER_NAME_TO_SEARCH, transmitterName)
        
        return RouterTransaction.with(AddTransmitterAT9Controller(this)).tag(CONTROLLER_TAG)
      }
    }
  }
}