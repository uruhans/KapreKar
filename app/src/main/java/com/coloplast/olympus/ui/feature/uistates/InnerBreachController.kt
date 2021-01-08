package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateInnerBreachBinding
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.feature.home.models.PatchVisible
import org.koin.core.inject

class InnerBreachController : BaseHomeUIControllerAppTour<ControllerStateInnerBreachBinding>(), PatchVisible {

  override fun attachVariables(viewBinding: ControllerStateInnerBreachBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_inner_breach
  }
  
  companion object {
    private const val TAG_CONTROLLER = "state_inner_breach_controller"
    fun routerTransaction() = RouterTransaction.with(InnerBreachController()).tag(TAG_CONTROLLER)
  }
}