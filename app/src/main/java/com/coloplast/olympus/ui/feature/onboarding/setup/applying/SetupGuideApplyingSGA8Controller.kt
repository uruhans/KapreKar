package com.coloplast.olympus.ui.feature.onboarding.setup.applying

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuideApplyingSga8Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.onboarding.setup.model.TransmitterCheckerSelectionType

class SetupGuideApplyingSGA8Controller :
  BaseController<ControllerGuideApplyingSga8Binding>(), TopMenuVisibilityListener {

  override fun attachVariables(viewBinding: ControllerGuideApplyingSga8Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerGuideApplyingSga8Binding) {
    /* no-op */
  }

  fun onClicked(selectionType: TransmitterCheckerSelectionType) {
    getListener(FlowListener::class.java)?.onNextClicked(selectionType)
  }

  override fun showCloseButton(): Boolean {
    return false
  }

  override fun getLayout(): Int {
    return R.layout.controller_guide_applying_sga8
  }

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_applying_sga8_controller"

    fun routerTransaction() = RouterTransaction.with(SetupGuideApplyingSGA8Controller())
      .tag(TAG_CONTROLLER)
  }
}