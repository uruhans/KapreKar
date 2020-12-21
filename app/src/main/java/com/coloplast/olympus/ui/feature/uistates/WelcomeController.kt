package com.coloplast.olympus.ui.feature.uistates

import androidx.core.view.doOnPreDraw
import androidx.core.view.postDelayed
import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateWelcomeBinding
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class WelcomeController : BaseHomeUIController<ControllerStateWelcomeBinding>(), PatchVisible {

  val shouldShowAppTourButton = ObservableBoolean(false)

  private var appTourStarted = false

  override fun attachVariables(viewBinding: ControllerStateWelcomeBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this

    viewBinding.root.doOnPreDraw { closeAfterPeriod(viewBinding) }
  }

  override fun onViewBound(viewBinding: ControllerStateWelcomeBinding) {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      it?.let { it1 -> shouldShowAppTourButton.set(it1.shouldShowAppTour()) }
    }
  }

  private fun closeAfterPeriod(viewBinding: ControllerStateWelcomeBinding) {
    viewBinding.root.postDelayed(MAX_VISIBLE_TIME) {
      router.popController(this)
    }
  }

  fun startAppTour() {
    (Navigation.getContentRouter()?.getControllerWithTag(HomeController.TAG_CONTROLLER) as? HomeController).let {
      appTourStarted = true
      it?.startAppTour()
    }
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_welcome
  }

  companion object {
    private const val TAG_CONTROLLER = "state_welcome_controller"
    private const val MAX_VISIBLE_TIME = 5000L
    fun routerTransaction() = RouterTransaction.with(WelcomeController()).tag(TAG_CONTROLLER)
  }
}