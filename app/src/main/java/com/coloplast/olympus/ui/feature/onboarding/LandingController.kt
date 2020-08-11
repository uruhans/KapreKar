package com.coloplast.olympus.ui.feature.onboarding

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerLandingBinding
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.feature.onboarding.createprofile.CreateUserFlowController
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.DataConsentFlowController
import com.coloplast.olympus.ui.feature.onboarding.login.LoginController
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideApplyingFlowController
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideIntroFlowController
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuidePreparingFlowController
import com.coloplast.olympus.ui.feature.onboarding.valueproposition.ValuePropositionFlowController
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterFlowController
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
    val routerTransaction = if (preferences.didSeeValueProposition.not()) {
      ValuePropositionFlowController.routerTransaction()
    }
    else if (preferences.getCurrentOnboardingProgress() == OnboardingProgression.NOT_STARTED) {
      null
    }
    else if (preferences.getCurrentOnboardingProgress() != OnboardingProgression.COMPLETED) {
      when (preferences.getCurrentOnboardingProgress()) {
        OnboardingProgression.TERMS_AND_CONSENT -> DataConsentFlowController.routerTransaction()
        OnboardingProgression.SETUP_GUIDE_START -> {
          GenericConfirmationController.routerTransaction(
            R.string.onboarding_chapter_intro_setup_guide,
            R.string.onboarding_chapter_intro_setup_guide_message,
            R.string.generic_get_started,
            imageId = R.drawable.ic_icon_guides,
            showCloseButton = false,
            showBackButton = false
          )
        }
        OnboardingProgression.SETUP_GUIDE_INTRODUCTION -> SetupGuideIntroFlowController.routerTransaction()
        OnboardingProgression.SETUP_GUIDE_CONNECTING -> AddTransmitterFlowController.routerTransaction()
        OnboardingProgression.SETUP_GUIDE_PREPARING -> SetupGuidePreparingFlowController.routerTransaction()
        OnboardingProgression.SETUP_GUIDE_APPLYING -> SetupGuideApplyingFlowController.routerTransaction()
        else -> null
      }
    }
    else {
      null
    }

    viewBinding.logInButton.postDelayed({
      routerTransaction?.let {
        getContentRouter().pushController(
          it.popChangeHandler(VerticalChangeHandler())
            .pushChangeHandler(VerticalChangeHandler())
        )
      }
    }, 1000)
  }

  fun onCreateProfileClicked() {
    getContentRouter().pushController(
      CreateUserFlowController.routerTransaction()
        .popChangeHandler(VerticalChangeHandler())
        .pushChangeHandler(VerticalChangeHandler())
    )

  }

  fun onLogInClicked() {
    getContentRouter().pushController(
      LoginController.getRouterTransaction()
        .popChangeHandler(HorizontalChangeHandler())
        .pushChangeHandler(HorizontalChangeHandler())
    )
  }

  override fun getLayout(): Int = R.layout.controller_landing

  companion object {
    private const val TAG_CONTROLLER = "landing_controller"

    fun routerTransaction() = RouterTransaction.with(LandingController())
      .tag(TAG_CONTROLLER)
  }
}