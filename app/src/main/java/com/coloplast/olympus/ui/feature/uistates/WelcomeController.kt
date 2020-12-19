package com.coloplast.olympus.ui.feature.uistates

import androidx.core.view.doOnPreDraw
import androidx.core.view.postDelayed
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

    viewBinding.root.doOnPreDraw { closeAfterPeriod(viewBinding) }
  }

  override fun onViewBound(viewBinding: ControllerStateWelcomeBinding) {

  }

  private fun closeAfterPeriod(viewBinding: ControllerStateWelcomeBinding) {
    viewBinding.root.postDelayed(MAX_VISIBLE_TIME) {
      if (!appTourStarted) {
        val shouldPopLastView = router.backstackSize == 1
        if (shouldPopLastView) {
          router.setPopsLastView(true)
        }

        router.popController(this)
      }
    }
  }

  fun onAppTourClicked() {
    appTourStarted = true
    startAppTour()
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