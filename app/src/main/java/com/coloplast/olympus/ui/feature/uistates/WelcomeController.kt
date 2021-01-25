package com.coloplast.olympus.ui.feature.uistates

import androidx.core.view.doOnPreDraw
import androidx.core.view.postDelayed
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateWelcomeBinding
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.feature.home.models.LastCheckVisible
import com.coloplast.olympus.ui.feature.home.models.PatchVisible
import org.koin.core.inject

class WelcomeController : BaseHomeUIControllerAppTour<ControllerStateWelcomeBinding>(), PatchVisible, LastCheckVisible {

  private val preferences: Preferences by inject()

  private var appTourStarted = false

  override fun attachVariables(viewBinding: ControllerStateWelcomeBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this

    viewBinding.root.doOnPreDraw { closeAfterPeriod(viewBinding) }
  }

  override fun onViewBound(viewBinding: ControllerStateWelcomeBinding) {
    preferences.didSeeWelcome = true
    super.onViewBound(viewBinding)
  }

  override fun lastCheckVisible(): Boolean {
    return true
  }

  private fun closeAfterPeriod(viewBinding: ControllerStateWelcomeBinding) {
    viewBinding.root.postDelayed(MAX_VISIBLE_TIME) {
      router.popController(this)
    }
  }

  override fun startAppTour() {
    appTourStarted = true
    super.startAppTour()
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