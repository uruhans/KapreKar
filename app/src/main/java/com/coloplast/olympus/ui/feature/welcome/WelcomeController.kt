package com.coloplast.olympus.ui.feature.welcome

import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerWelcomeBinding
import com.coloplast.olympus.ui.feature.guides.flows.tour.AppTourFlowController
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.home.models.PatchVisible
import com.coloplast.olympus.ui.feature.uistates.BaseHomeUIController

class WelcomeController : BaseHomeUIController<ControllerWelcomeBinding>(), PatchVisible {

  private val isVisible = ObservableBoolean(false)

  private var appTourStarted = false

  override fun attachVariables(viewBinding: ControllerWelcomeBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  fun onAppTourClicked() {
    appTourStarted = true
    val (selectedRouter, routerTransaction) = Pair(getContentRouter(), AppTourFlowController.routerTransaction())
    selectedRouter.pushController(routerTransaction
      .pushChangeHandler(HorizontalChangeHandler())
      .popChangeHandler(HorizontalChangeHandler()))
  }

  override fun getLayout(): Int {
    return R.layout.controller_welcome
  }

  override fun patchVisible(): Boolean {
    return true
  }

  override fun onViewBound(viewBinding: ControllerWelcomeBinding) {
    val (routerTransaction, delay) = Pair(HomeController.routerTransaction(), 5000L)
    isVisible.set(routerTransaction == null)
    routerTransaction?.let {
      viewBinding.appTour.postDelayed({
        if (!appTourStarted) {
          router.setPopsLastView(true)
          router.popCurrentController()
          router.setPopsLastView(false)
          getContentRouter().pushController(
            it.popChangeHandler(FadeChangeHandler())
              .pushChangeHandler(FadeChangeHandler()))
        }
      }, delay)
    }
  }

  companion object {
    private const val TAG_CONTROLLER = "state_welcome_controller"
    fun routerTransaction() = RouterTransaction.with(WelcomeController()).tag(TAG_CONTROLLER)
  }
}