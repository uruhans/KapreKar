package com.coloplast.olympus.ui.feature.onboarding

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
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

  override fun attachVariables(viewBinding: ControllerLandingBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerLandingBinding) {
    var delay = 1000L

    val routerTransaction = if (preferences.didSeeValueProposition.not()) {
      ValuePropositionFlowController.routerTransaction()
    }
    else if (preferences.getCurrentOnboardingProgress() == OnboardingProgression.NOT_STARTED) {
      null
    }
    else if (preferences.userToken.isNotBlank()) {
      if (preferences.getCurrentOnboardingProgress() != OnboardingProgression.COMPLETED) {
        val flowTransaction = when (preferences.getCurrentOnboardingProgress()) {
          OnboardingProgression.SETUP_GUIDE_START,
          OnboardingProgression.SETUP_GUIDE_INTRODUCTION -> SetupGuideIntroFlowController.routerTransaction()
          OnboardingProgression.SETUP_GUIDE_CONNECTING -> SetupGuideConnectionFlowController.routerTransaction()
          OnboardingProgression.SETUP_GUIDE_PREPARING -> SetupGuidePreparingFlowController.routerTransaction()
          OnboardingProgression.SETUP_GUIDE_APPLYING -> SetupGuideApplyingFlowController.routerTransaction()
          else -> null
        }

        delay = 50L
        flowTransaction
      }
      else if (preferences.getCurrentOnboardingProgress() == OnboardingProgression.COMPLETED) {
        router.setPopsLastView(true)
        router.popCurrentController()
        router.setPopsLastView(false)
        HomeController.routerTransaction()
      }
      else {
        null
      }
    }
    else {
      null
    }

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