package com.coloplast.olympus.ui.feature.onboarding.setup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.feature.onboarding.setup.intro.SetupGuideIntroSGI1Controller
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideIntroductionFlow

class SetupGuideIntroFactoryImpl : FlowFactory<EmptyModel>() {

  private val model = EmptyModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    val screenName = SetupGuideIntroductionFlow.fromIndex(currentIndex).name
    return when(SetupGuideIntroductionFlow.fromIndex(currentIndex)) {
      SetupGuideIntroductionFlow.SetupGuideSG1 -> {
        GenericConfirmationController.routerTransaction(
          R.string.onboarding_chapter_intro_setup_guide,
          R.string.onboarding_chapter_intro_setup_guide_message,
          R.string.generic_get_started,
          R.drawable.ic_sidemenu_guides,
          showBackButton = false,
          showCloseButton = false
        )
      }
      SetupGuideIntroductionFlow.SetupGuideSG2 -> SetupGuideSG2.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideSGI1 -> SetupGuideIntroSGI1Controller.routerTransaction()
      SetupGuideIntroductionFlow.SetupGuideInTheBoxSGI6 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.sg_i6_whatsinthebox,
        titleId = R.string.onboarding_chapter_intro_box_title,
        descriptionId = R.string.onboarding_chapter_intro_box_description,
        position = 0,
        totalCount = 0,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideSensorLayerSGI7 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.sg_i7_sensorlayer,
        titleId = R.string.onboarding_chapter_intro_sensor_layer_title,
        descriptionId = R.string.onboarding_chapter_intro_sensor_layer_description,
        position = 1,
        totalCount = 4,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideSensorTechSGI8 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i8_advsensortech,
        titleId = R.string.onboarding_chapter_intro_sensor_technology_title,
        descriptionId = R.string.onboarding_chapter_intro_sensor_technology_description,
        position = 2,
        totalCount = 4,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideOuterSensorsSGI9 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i9_inneroutersensor,
        titleId = R.string.onboarding_chapter_intro_inner_outer_title,
        descriptionId = R.string.onboarding_chapter_intro_inner_outer_description,
        position = 3,
        totalCount = 4,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideConnectorFlapSGI10 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i10_connectorflap,
        titleId = R.string.onboarding_chapter_intro_connector_flap_title,
        descriptionId = R.string.onboarding_chapter_intro_connector_flap_description,
        position = 4,
        totalCount = 4,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideTransmitterSGI11 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.sg_i11_yourtransmitter,
        titleId = R.string.onboarding_chapter_intro_transmitter_title,
        descriptionId = R.string.onboarding_chapter_intro_transmitter_description,
        position = 1,
        totalCount = 5,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideLiftingTransmitterLidSGI12 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i12_openingtmlid,
        titleId = R.string.onboarding_chapter_intro_transmitter_lid_title,
        descriptionId = R.string.onboarding_chapter_intro_transmitter_lid_description,
        position = 2,
        totalCount = 5,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideOpeningTransmitterLidSGI13 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i13_tm_opening,
        titleId = R.string.guide_co9_title,
        descriptionId = R.string.guide_co9_description,
        position = 3,
        totalCount = 5,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideDisengagingTransmitterLidSGI14 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i14_disengaging_tm_lid,
        titleId = R.string.guide_co10_title,
        descriptionId = R.string.guide_co10_description,
        position = 4,
        totalCount = 5,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideClosingTransmitterLidSGI15 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i15_closing_tm_lid,
        titleId = R.string.guide_co11_title,
        descriptionId = R.string.guide_co11_description,
        position = 5,
        totalCount = 5,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuideChargerSGI16 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i16_chargerandcable,
        titleId = R.string.onboarding_chapter_intro_charging_title,
        descriptionId = R.string.onboarding_chapter_intro_charging_description,
        position = 1,
        totalCount = 2,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideIntroductionFlow.SetupGuidePairingSGI17 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i17_chargingandpairing,
        titleId = R.string.onboarding_chapter_intro_pairing_title,
        descriptionId = R.string.onboarding_chapter_intro_pairing_description,
        position = 2,
        totalCount = 2,
        screenName = screenName,
        showCloseButton = false
      )
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