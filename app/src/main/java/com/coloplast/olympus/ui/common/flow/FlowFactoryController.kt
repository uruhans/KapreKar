package com.coloplast.olympus.ui.common.flow

import android.os.Bundle
import androidx.annotation.CallSuper
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.BaseFlowController
import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel

abstract class FlowFactoryController<T : FlowFactoryModel>(args: Bundle? = null) : BaseFlowController(args),
    FlowFactory.Listener<T> {
  
  init {
    showToolbar.set(true)
  }
  
  abstract fun getFlowFactory() : FlowFactory<T>

  /**
   * This method will be hit after the new controller has been pushed onto the stack
   *
   * @return the controller that is now being shown
   */
  open fun afterControllerPushed(topController: Controller) { }

  /**
   * This method will be hit after a controller has been popped (going back), and will return the NEW topController
   *
   * @return the controller that is *now* on top or null if the flow is closing
   */
  open fun afterControllerPopped(topController: Controller?) { }

  @CallSuper
  override fun startFlow(flowChildRouter: Router) {
    getFlowFactory().setFlowFactoryListener(this)
    getFlowFactory().restart()
  }
  
  override fun next(routerTransaction: RouterTransaction) {
    pushController(routerTransaction)

    afterControllerPushed(routerTransaction.controller)
  }

  @CallSuper
  override fun onFlowCompleted(data: T) {
    closeFlow()
  }
  
  override fun onNextClicked(result: Any?) {
    getFlowFactory().onResult(result)
  }
 
  override fun handleBack(): Boolean {
    getFlowFactory().previous()

    if(super.handleBack()) {
      afterControllerPopped(getFlowChildRouter().backstack.lastOrNull()?.controller)
      return true
    }

    return false
  }
}