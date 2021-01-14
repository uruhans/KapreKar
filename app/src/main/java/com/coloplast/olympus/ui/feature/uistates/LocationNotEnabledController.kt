package com.coloplast.olympus.ui.feature.uistates

import android.content.Intent
import android.provider.Settings
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateLocationNotEnabledBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class LocationNotEnabledController : BaseHomeUIController<ControllerStateLocationNotEnabledBinding>(),
  PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateLocationNotEnabledBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateLocationNotEnabledBinding) {
    // no-op
  }

  override fun patchInError(): Boolean {
    return true
  }

  fun enableLocationClicked() {
    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).also {
      it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(it)
    }
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_location_not_enabled
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_location_not_enabled_controller"
    fun routerTransaction() = RouterTransaction.with(LocationNotEnabledController()).tag(TAG_CONTROLLER)
  }
}