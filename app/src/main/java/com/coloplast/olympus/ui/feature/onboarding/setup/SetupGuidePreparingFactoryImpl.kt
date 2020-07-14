package com.coloplast.olympus.ui.feature.onboarding.setup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.SimpleInfoPageController
import com.coloplast.olympus.ui.common.flow.EmptyModel
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuidePreparingFlow
import com.coloplast.olympus.ui.feature.onboarding.setup.preparing.SetupGuidePreparingSGP2Controller
import com.coloplast.olympus.ui.feature.onboarding.setup.preparing.SetupGuidePreparingSGP3Controller
import com.coloplast.olympus.ui.feature.onboarding.setup.preparing.SetupGuidePreparingSGP4Controller
import com.coloplast.olympus.ui.feature.onboarding.setup.preparing.SetupGuidePreparingSGP5Controller

class SetupGuidePreparingFactoryImpl : FlowFactory<EmptyModel>() {

  private val model = EmptyModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when(SetupGuidePreparingFlow.fromIndex(currentIndex)) {
      SetupGuidePreparingFlow.SetupGuideSG4 -> SetupGuideSG4.routerTransaction()
      SetupGuidePreparingFlow.SetupGuideChangingSGP1 -> {
          SimpleInfoPageController.routerTransaction(
            R.drawable.ic_sidemenu_settings, // TODO - need correct image
            R.string.onboarding_chapter_preparing_change_title,
            R.string.onboarding_chapter_preparing_change_description,
            0,
            0,
            showBackButton = true,
            showCloseButton = false
          )
      }
      SetupGuidePreparingFlow.SetupGuideComeBackLaterSGP1_2 -> {
        SimpleInfoPageController.routerTransaction(
          R.drawable.ic_sidemenu_settings, // TODO - need correct image
          R.string.onboarding_chapter_preparing_come_back_later_title,
          R.string.onboarding_chapter_preparing_come_back_later_description,
          0,
          0,
          showBackButton = true,
          showCloseButton = false
        )
      }
      SetupGuidePreparingFlow.SetupGuideRemoveSGP2 -> SetupGuidePreparingSGP2Controller.routerTransaction()
      SetupGuidePreparingFlow.SetupGuidePlaceSLSGP3 -> SetupGuidePreparingSGP3Controller.routerTransaction()
      SetupGuidePreparingFlow.SetupGuidePressDownSGP4 -> SetupGuidePreparingSGP4Controller.routerTransaction()
      SetupGuidePreparingFlow.SetupGuideSecureSGP5 -> SetupGuidePreparingSGP5Controller.routerTransaction()
      SetupGuidePreparingFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    /* no-op */
  }

  override fun getData(): EmptyModel {
    return model
  }
}