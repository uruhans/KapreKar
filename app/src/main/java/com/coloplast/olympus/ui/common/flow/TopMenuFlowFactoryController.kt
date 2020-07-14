package com.coloplast.olympus.ui.common.flow

import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import com.coloplast.olympus.shared.models.flow.FlowFactoryModel

abstract class TopMenuFlowFactoryController<T : FlowFactoryModel>(args: Bundle? = null) : FlowFactoryController<T>(args) {

  override fun afterControllerPushed(topController: Controller) {
    topMenuVisibility(topController)
  }
  override fun afterControllerPopped(topController: Controller?) {
    topMenuVisibility(topController)
  }
  private fun topMenuVisibility(topController: Controller?) {
    (topController as? TopMenuVisibilityListener)?.let {
      showBackButton(it.showBackButton())
      showCloseButton(it.showCloseButton())
    }
  }
}