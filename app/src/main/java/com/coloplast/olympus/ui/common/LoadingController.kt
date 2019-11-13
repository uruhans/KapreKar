package com.coloplast.olympus.ui.common

import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerLoadingBinding

class LoadingController : BaseController<ControllerLoadingBinding>() {
  
  override fun attachVariables(viewBinding: ControllerLoadingBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerLoadingBinding) {
    // no-op
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_loading
  }
  
  fun onOverlayClicked() {
    // no-op
  }
  
  companion object {
    private const val TAG = "LoadingController"
    
    private fun get(): RouterTransaction {
      return RouterTransaction.with(LoadingController())
          .tag(TAG)
          .pushChangeHandler(FadeChangeHandler(false))
          .popChangeHandler(FadeChangeHandler())
    }
    
    fun toggle(shouldShow: Boolean, router: Router) {
      val controller = router.getControllerWithTag(TAG)
      if (shouldShow && controller == null) {
        router.pushController(get())
      }
      else if (!shouldShow && controller != null) {
        router.popController(controller)
      }
    }
  }
}