package com.coloplast.olympus.ui.feature.uistates

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateWelcomeBinding
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class WelcomeController : BaseHomeUIControllerAppTour<ControllerStateWelcomeBinding>(), PatchVisible {

  private var appTourStarted = false

  override fun attachVariables(viewBinding: ControllerStateWelcomeBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  fun onAppTourClicked() {
    appTourStarted = true
    startAppTour()
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_welcome
  }

  override fun onViewBound(viewBinding: ControllerStateWelcomeBinding) {
    val (routerTransaction, delay) = Pair(HomeController.routerTransaction(), 5000L)
      viewBinding.includeBtn.appTourBtn.postDelayed({
        if (!appTourStarted) {
          router.setPopsLastView(true)
          router.popCurrentController()
          router.setPopsLastView(false)
          getContentRouter().pushController(
            routerTransaction.popChangeHandler(FadeChangeHandler())
              .pushChangeHandler(FadeChangeHandler()))
        }
      }, delay)
  }

  companion object {
    private const val TAG_CONTROLLER = "state_welcome_controller"
    fun routerTransaction() = RouterTransaction.with(WelcomeController()).tag(TAG_CONTROLLER)
  }
}