package com.coloplast.olympus.ui.feature.uistates

import androidx.annotation.CallSuper
import androidx.databinding.ObservableBoolean
import androidx.databinding.ViewDataBinding
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController

abstract class BaseHomeUIControllerAppTour<T : ViewDataBinding>() : BaseHomeUIController<T>() {

  val shouldShowAppTourButton = ObservableBoolean(false)

  @CallSuper
  override fun onViewBound(viewBinding: T) {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.let { homeController -> shouldShowAppTourButton.set(homeController.shouldShowAppTourButton()) }
    }
  }

  open fun startAppTour() {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.startAppTour()
    }
  }
}