package com.coloplast.olympus.ui.feature.onboarding

import android.content.Context
import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.ServiceStateDispatcher
import com.coloplast.olympus.databinding.ControllerLandingBinding
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.login.LoginController
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideApplyingFlowController
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideConnectionFlowController
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideIntroFlowController
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuidePreparingFlowController
import com.coloplast.olympus.ui.feature.onboarding.signup.SignupFlowController
import com.coloplast.olympus.ui.feature.onboarding.valueproposition.ValuePropositionFlowController
import org.koin.core.inject

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 30/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LandingController : BaseController<ControllerLandingBinding>() {

  private val preferences: Preferences by inject()
  private val serviceStateDispatcher: ServiceStateDispatcher by inject()

  val isVisible = ObservableBoolean(false) // by default, we hide the content of this view

  override fun attachVariables(viewBinding: ControllerLandingBinding) {
    viewBinding.controller = this
  }

  override fun onContextAvailable(context: Context) {
    super.onContextAvailable(context)
    // make sure that the service is started at this point
    // if the user has just logged out, it will not be started
    serviceStateDispatcher.serviceRelay.accept(ServiceStateDispatcher.ServiceState.START_SERVICE)
  }

  override fun onViewBound(viewBinding: ControllerLandingBinding) {

    val (routerTransaction, delay) = when (preferences.didSeeValueProposition) {
      false -> Pair(ValuePropositionFlowController.routerTransaction(), 1000L)
      true -> if (preferences.getToken().isNotBlank()) {
        when (preferences.getCurrentOnboardingProgress()) {
          OnboardingProgression.NOT_STARTED, // freshly installed app and login with existing user.
          OnboardingProgression.SETUP_GUIDE_START,
          OnboardingProgression.SETUP_GUIDE_INTRODUCTION -> Pair(
            SetupGuideIntroFlowController.routerTransaction(),
            50L
          )
          OnboardingProgression.SETUP_GUIDE_CONNECTING -> Pair(
            SetupGuideConnectionFlowController.routerTransaction(),
            50L
          )
          OnboardingProgression.SETUP_GUIDE_PREPARING -> Pair(
            SetupGuidePreparingFlowController.routerTransaction(),
            50L
          )
          OnboardingProgression.SETUP_GUIDE_APPLYING -> Pair(
            SetupGuideApplyingFlowController.routerTransaction(),
            50L
          )
          OnboardingProgression.COMPLETED -> {
            router.setPopsLastView(true)
            router.popCurrentController()
            router.setPopsLastView(false)
            Pair(HomeController.routerTransaction(), 1000L)
          }
          else -> Pair(null, 0L)
        }
      }
      else { Pair(null, 0L) }
    }

    isVisible.set(routerTransaction == null) // if there is no pending transaction we show the content of this view

    routerTransaction?.let {
      viewBinding.logInButton.postDelayed({
        getContentRouter().pushController(
          it.popChangeHandler(FadeChangeHandler())
            .pushChangeHandler(FadeChangeHandler())
        )
      }, delay)
    }
  }

  fun onCreateProfileClicked() {
    getContentRouter().pushController(
      SignupFlowController.routerTransaction()
        .popChangeHandler(VerticalChangeHandler())
        .pushChangeHandler(VerticalChangeHandler())
    )
  }

  fun onLogInClicked() {
    getContentRouter().pushController(
      LoginController.getRouterTransaction()
        .popChangeHandler(VerticalChangeHandler())
        .pushChangeHandler(VerticalChangeHandler())
    )
  }

  override fun getLayout(): Int = R.layout.controller_landing

  companion object {
    private const val TAG_CONTROLLER = "landing_controller"

    fun routerTransaction() = RouterTransaction.with(LandingController())
      .tag(TAG_CONTROLLER)
  }
}