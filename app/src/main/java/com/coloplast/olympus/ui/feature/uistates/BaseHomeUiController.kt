package com.coloplast.olympus.ui.feature.uistates

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import com.coloplast.olympus.BR
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController

abstract class BaseHomeUIController<T : ViewDataBinding> : BaseController<T> {
  
  constructor() : super()
  constructor(args: Bundle?) : super(args)
  
  @CallSuper
  override fun attachVariables(viewBinding: T) {
    viewBinding.setVariable(BR.home, Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController)
  }
}