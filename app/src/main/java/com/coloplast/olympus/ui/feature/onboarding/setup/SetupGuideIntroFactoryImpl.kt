package com.coloplast.olympus.ui.feature.onboarding.setup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.SimpleInfoPageController
import com.coloplast.olympus.ui.common.flow.EmptyModel
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.setup.intro.SetupGuideIntroSGI1
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideIntroductionFlow

class SetupGuideIntroFactoryImpl : FlowFactory<EmptyModel>() {

  private val model = EmptyModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when(SetupGuideIntroductionFlow.fromIndex(currentIndex)) {
      SetupGuideIntroductionFlow.SetupGuideSG1 -> {
        GenericConfirmationController.routerTransaction(
          R.string.onboarding_chapter_intro_setup_guide,
          R.string.onboarding_chapter_intro_setup_guide_message,
          R.string.generic_get_started,
          R.drawable.ic_sidemenu_guides
        )
      }
      SetupGuideIntroductionFlow.SetupGuideSG2 -> SetupGuideSG2.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideSGI1 -> SetupGuideIntroSGI1.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideBluetoothSGI2 -> {
        SimpleInfoPageController.routerTransaction(
          iconId = R.drawable.ic_bluetooth,
          titleId = R.string.onboarding_chapter_intro_bluetooth_header,
          desciptionId = R.string.onboarding_chapter_intro_bluetooth_description,
          position = 1,
          totalCount = 1,
          showBackButton = false,
          showCloseButton = false
        )
      }
      SetupGuideIntroductionFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    /* no-op */
  }

  override fun getData(): EmptyModel {
    return model
  }
}