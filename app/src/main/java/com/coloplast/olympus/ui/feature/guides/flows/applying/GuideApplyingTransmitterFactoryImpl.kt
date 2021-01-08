package com.coloplast.olympus.ui.feature.guides.flows.applying

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.setup.applying.AwaitingConnectionController
import com.coloplast.olympus.ui.feature.onboarding.setup.model.ApplyingFlowModel

class GuideApplyingTransmitterFactoryImpl : FlowFactory<ApplyingFlowModel>() {

  private val model = ApplyingFlowModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    val transitionTag = GuideApplyingTransmitterFlow.fromIndex(currentIndex).name
    return when (GuideApplyingTransmitterFlow.fromIndex(currentIndex)) {
      GuideApplyingTransmitterFlow.GuideAG1 -> InfoPageController.routerTransaction(
        iconId = R.drawable.ic_icon_guides,
        titleId = R.string.guide_ap9_title,
        descriptionId = R.string.guide_ap9_intro_screen_subtitle,
        positiveButtonTextId = R.string.generic_get_started
      )
      GuideApplyingTransmitterFlow.GuideAG2 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.ag3_detach_transmitter,
        titleId = R.string.guide_ag2_title,
        descriptionId = R.string.guide_ag2_description,
        position = 1,
        screenName = transitionTag,
        totalCount = 13
      )

      GuideApplyingTransmitterFlow.GuideAG2_5 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_p2_3_lift_sl_connectorflap,
        titleId = R.string.onboarding_chapter_preparing_sl_size_lift_title,
        descriptionId = R.string.onboarding_chapter_preparing_sl_size_lift_description,
        position = 2,
        screenName = transitionTag,
        totalCount = 13
      )

      GuideApplyingTransmitterFlow.GuideAG3 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_p2_changecoming,
        titleId = R.string.onboarding_chapter_preparing_removing_title,
        descriptionId = R.string.onboarding_chapter_preparing_removing_description,
        position = 2,
        screenName = transitionTag,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.GuideAG4 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_p3_placeslonbp,
        titleId = R.string.onboarding_chapter_preparing_place_sl_title,
        descriptionId = R.string.onboarding_chapter_preparing_place_sl_description,
        position = 3,
        screenName = transitionTag,
        totalCount = 13
      )

      GuideApplyingTransmitterFlow.GuideAG5 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_p4_pressdownsl,
        titleId = R.string.onboarding_chapter_preparing_press_down_title,
        descriptionId = R.string.onboarding_chapter_preparing_press_down_description,
        position = 4,
        screenName = transitionTag,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.GuideAG6 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_p5_ensuresecureadhesion,
        titleId = R.string.onboarding_chapter_preparing_secure_title,
        descriptionId = R.string.onboarding_chapter_preparing_secure_description,
        position = 5,
        screenName = transitionTag,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.GuideAG7 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a1_removeslreleaseliner,
        titleId = R.string.onboarding_chapter_applying_remove_sl_title,
        descriptionId = R.string.onboarding_chapter_applying_remove_sl_description,
        position = 6,
        screenName = transitionTag,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.GuideAG8 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a2_applytobody,
        titleId = R.string.onboarding_chapter_applying_apply_title,
        descriptionId = R.string.onboarding_chapter_applying_apply_description,
        position = 7,
        screenName = transitionTag,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.GuideAG9 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a3_liftconnectorflap,
        titleId = R.string.onboarding_chapter_applying_connector_flap_title,
        descriptionId = R.string.onboarding_chapter_applying_connector_flap_description,
        position = 8,
        screenName = transitionTag,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.GuideAG10 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a_tmh4_lifttmlid,
        titleId = R.string.onboarding_chapter_applying_lift_tm_lid_title,
        descriptionId = R.string.onboarding_chapter_applying_lift_tm_lid_description,
        position = 9,
        screenName = transitionTag,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.GuideAG11 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a7_identifyreverse,
        titleId = R.string.onboarding_chapter_applying_identify_dark_title,
        descriptionId = R.string.onboarding_chapter_applying_identify_dark_description,
        screenName = transitionTag,
        position = 10,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.GuideAG12 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a6_locatetmopening,
        titleId = R.string.onboarding_chapter_applying_locating_opening_title,
        descriptionId = R.string.onboarding_chapter_applying_locating_opening_description,
        screenName = transitionTag,
        position = 11,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.GuideAG14 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a12_attach_tm_flap,
        titleId = R.string.onboarding_chapter_applying_attach_title,
        descriptionId = R.string.onboarding_chapter_applying_attach_description,
        screenName = transitionTag,
        position = 12,
        totalCount = 13
      )
      GuideApplyingTransmitterFlow.AwaitingConnection -> AwaitingConnectionController.routerTransaction()
      GuideApplyingTransmitterFlow.GuideAG15 -> {
        GenericConfirmationController.routerTransaction(
          titleId = R.string.onboarding_chapter_applying_tm_not_attached_title,
          descriptionId = R.string.onboarding_chapter_applying_tm_not_attached_description,
          buttonId = R.string.guide_ag14_2_button,
          imageId = R.drawable.ic_close,
          imageBackgroundDrawable = R.drawable.shape_red_circle,
          imageTint = R.color.confirmation_icon_red
        )
      }
      GuideApplyingTransmitterFlow.SetupCompleted -> {
        GenericConfirmationController.routerTransaction(
          R.string.guide_ap14_title,
          R.string.guide_ap14_subtitle,
          R.string.guide_ap14_button,
          imageId = R.drawable.ic_checkmark
        )
      }
      GuideApplyingTransmitterFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    @Suppress("NON_EXHAUSTIVE_WHEN")
    when (GuideApplyingTransmitterFlow.fromIndex(currentIndex)) {
      GuideApplyingTransmitterFlow.AwaitingConnection -> {
        model.connectionSuccessful = result as Boolean
      }
      GuideApplyingTransmitterFlow.GuideAG15 -> {
        model.retryConnection = result as Boolean
      }
    }
  }

  override fun getData(): ApplyingFlowModel {
    return model
  }
}