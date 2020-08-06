package com.coloplast.olympus.ui.feature.onboarding.setup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.SimpleInfoPageController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.feature.onboarding.setup.intro.*
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
      SetupGuideIntroductionFlow.SetupGuideSGI1 -> SetupGuideIntroSGI1Controller.routerTransaction()
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
      SetupGuideIntroductionFlow.SetupGuideInTheBoxSGI6 -> SetupGuideIntroSGI6Controller.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideSensorLayerSGI7 -> SetupGuideIntroSGI7Controller.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideSensorTechSGI8 -> SetupGuideIntroSGI8Controller.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideOuterSensorsSGI9 -> SetupGuideIntroSGI9Controller.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideConnectorFlapSGI10 -> SetupGuideIntroSGI10Controller.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideTransmitterSGI11 -> SetupGuideIntroSGI11Controller.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideOpeningLidSGI12 -> SetupGuideIntroSGI12Controller.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideChargerSGI13 -> SetupGuideIntroSGI13Controller.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuidePairingSGI14 -> SetupGuideIntroSGI14Controller.routerTransaction()
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