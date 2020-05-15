package com.coloplast.olympus.ui.common.flow

import android.os.Bundle
import androidx.annotation.CallSuper
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.BaseFlowController

abstract class FlowFactoryController<T : FlowFactoryModel>(args: Bundle) : BaseFlowController(args),
    FlowFactory.Listener<T> {
  
  init {
    showToolbar.set(true)
  }
  
  abstract fun getFlowFactory() : FlowFactory<T>

  @CallSuper
  override fun startFlow(flowChildRouter: Router) {
    setFlowFactoryListener()
    getFlowFactory().restart()
  }
  
  private fun setFlowFactoryListener() {
    getFlowFactory().setFlowFactoryListener(this)
  }
  
  override fun next(routerTransaction: RouterTransaction) {
    pushController(routerTransaction)
  }
  
  override fun onFlowCompleted(data: T) {
    closeFlow()
  }
  
  override fun onNextClicked(result: Any?) {
    getFlowFactory().onResult(result)
  }
 
  override fun handleBack(): Boolean {
    getFlowFactory().previous()
    return super.handleBack()
  }
}