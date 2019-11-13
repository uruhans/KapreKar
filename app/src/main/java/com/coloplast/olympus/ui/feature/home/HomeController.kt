package com.coloplast.olympus.ui.feature.home

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerHomeBinding
import com.coloplast.olympus.ui.common.BaseController

class HomeController : BaseController<ControllerHomeBinding> {
  
  constructor() : super()
  constructor(args: Bundle?) : super(args)
  
  override fun attachVariables(viewBinding: ControllerHomeBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerHomeBinding) {
  
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_home
  }
  
  companion object {
    const val TAG_CONTROLLER = "home_controller"
    
    fun routerTransaction() = RouterTransaction.with(HomeController()).tag(TAG_CONTROLLER)
  }
  
}