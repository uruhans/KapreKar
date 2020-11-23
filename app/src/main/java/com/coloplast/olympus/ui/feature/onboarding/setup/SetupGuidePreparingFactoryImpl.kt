package com.coloplast.olympus.ui.feature.onboarding.setup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuidePreparingFlow

class SetupGuidePreparingFactoryImpl : FlowFactory<EmptyModel>() {

  private val model = EmptyModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (SetupGuidePreparingFlow.fromIndex(currentIndex)) {
      SetupGuidePreparingFlow.SetupGuideSG4 -> SetupGuideSG4.routerTransaction()
      SetupGuidePreparingFlow.SetupGuideChangingSGP1 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_info_circle,
          R.string.onboarding_chapter_preparing_change_title,
          R.string.onboarding_chapter_preparing_change_description,
          positiveButtonTextId = R.string.generic_continue,
          negativeButtonTextId = R.string.onboarding_chapter_preparing_change_come_back_later_button,
          showCloseButton = false
        )
      }
      SetupGuidePreparingFlow.SetupGuideComeBackLaterSGP1_2 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_coffee,
          R.string.onboarding_chapter_preparing_come_back_later_title,
          R.string.onboarding_chapter_preparing_come_back_later_description,
          R.string.onboarding_chapter_preparing_come_back_later_button,
          showCloseButton = false
        )
      }
      SetupGuidePreparingFlow.SetupGuideDirectContact -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sensor_layer,
          R.string.onboarding_chapter_preparing_direct_contact_title,
          R.string.onboarding_chapter_preparing_direct_contact_description,
          R.string.onboarding_confirmation_button,
          showCloseButton = false
        )
      }
      SetupGuidePreparingFlow.SetupGuideRemoveSGP2 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_p2_changecoming,
        titleId = R.string.onboarding_chapter_preparing_removing_title,
        descriptionId = R.string.onboarding_chapter_preparing_removing_description,
        position = 1,
        totalCount = 4,
        showCloseButton = false
      )
      SetupGuidePreparingFlow.SetupGuidePlaceSLSGP3 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_p3_placeslonbp,
        titleId = R.string.onboarding_chapter_preparing_place_sl_title,
        descriptionId = R.string.onboarding_chapter_preparing_place_sl_description,
        position = 2,
        totalCount = 4,
        showCloseButton = false
      )
      SetupGuidePreparingFlow.SetupGuidePressDownSGP4 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_p4_pressdownsl,
        titleId = R.string.onboarding_chapter_preparing_press_down_title,
        descriptionId = R.string.onboarding_chapter_preparing_press_down_description,
        position = 3,
        totalCount = 4,
        showCloseButton = false
      )
      SetupGuidePreparingFlow.SetupGuideSecureSGP5 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_p5_ensuresecureadhesion,
        titleId = R.string.onboarding_chapter_preparing_secure_title,
        descriptionId = R.string.onboarding_chapter_preparing_secure_description,
        position = 4,
        totalCount = 4,
        showCloseButton = false
      )
      SetupGuidePreparingFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    if (SetupGuidePreparingFlow.fromIndex(currentIndex) == SetupGuidePreparingFlow.SetupGuideChangingSGP1) {
      if (result == true) {
        skipNext()
      }
    }
  }

  override fun getData(): EmptyModel {
    return model
  }
}