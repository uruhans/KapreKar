package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateConnectingBinding
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class ConnectingController : BaseHomeUIController<ControllerStateConnectingBinding>(),
  PatchVisible {

  override fun onViewBound(viewBinding: ControllerStateConnectingBinding) {
    // no-op
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_connecting
  }

  override fun patchVisible(): Boolean {
    return false
  }

  companion object {
    private const val TAG_CONTROLLER = "state_connecting_controller"
    fun routerTransaction() = RouterTransaction.with(ConnectingController()).tag(TAG_CONTROLLER)
  }
}