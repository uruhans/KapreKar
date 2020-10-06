package com.coloplast.olympus.ui.feature.uistates

import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateAppUpdateAvailableBinding
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class AppUpdateAvailableController : BaseHomeUIController<ControllerStateAppUpdateAvailableBinding>(), PatchVisible {
  
  override fun attachVariables(viewBinding: ControllerStateAppUpdateAvailableBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerStateAppUpdateAvailableBinding) {
    // no-op
  }
  
  fun onUpdateAppClicked() {
    with(Intent(Intent.ACTION_VIEW)) {
      data = Uri.parse("market://details?id=" + AppSettings.applicationContext.packageName)
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
      startActivity(this)
    }
  }
  
  override fun patchInError(): Boolean {
    return true
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_state_app_update_available
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_app_update_available_controller"
    fun routerTransaction() = RouterTransaction.with(AppUpdateAvailableController()).tag(TAG_CONTROLLER)
  }
}