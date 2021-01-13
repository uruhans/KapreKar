package com.coloplast.olympus.ui.feature.uistates

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateBluetoothNotEnabledBinding
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class BluetoothNotEnabledController : BaseHomeUIController<ControllerStateBluetoothNotEnabledBinding>(),
  PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateBluetoothNotEnabledBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateBluetoothNotEnabledBinding) {
    // no-op
  }

  override fun patchVisible(): Boolean {
    return true
  }

  override fun patchInError(): Boolean {
    return true
  }

  fun enableBluetoothClicked() {
    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).also {
      startActivityForResult(it, HomeController.REQUEST_CODE_BLUETOOTH_ENABLE)
    }
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_bluetooth_not_enabled
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_bluetooth_not_enabled_controller"
    fun routerTransaction() = RouterTransaction.with(BluetoothNotEnabledController()).tag(TAG_CONTROLLER)
  }
}