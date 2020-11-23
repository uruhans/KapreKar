package com.coloplast.olympus.ui.feature.guides.flows.applying

import android.os.Bundle
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
    return when (GuideApplyingTransmitterFlow.fromIndex(currentIndex)) {
      GuideApplyingTransmitterFlow.GuideAG1 -> InfoPageController.routerTransaction(
        iconId = R.drawable.ic_icon_guides,
        titleId = R.string.guide_ap9_title,
        descriptionId = R.string.guide_ap9_intro_screen_subtitle,
        positiveButtonTextId = R.string.generic_get_started,
        showCloseButton = true,
        showBackButton = true
      )
      GuideApplyingTransmitterFlow.GuideAG2 -> {
        class MediaControllerGuideAG2 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG2.name) }) {
          override val resourceId: Int
            get() = R.raw.ag3_detach_transmitter
          override val titleId: Int
            get() = R.string.guide_ag2_title
          override val descriptionId: Int
            get() = R.string.guide_ag2_description
          override val position: Int
            get() = 1
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerGuideAG2())
      }
      GuideApplyingTransmitterFlow.GuideAG3 -> {
        class MediaControllerAG3 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG3.name) }) {
          override val resourceId: Int
            get() = R.raw.ag3_remove_release_liner
          override val titleId: Int
            get() = R.string.onboarding_chapter_preparing_removing_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_preparing_removing_description
          override val position: Int
            get() = 2
          override val totalCount: Int
            get() = 12
        }
        RouterTransaction.with(MediaControllerAG3())
      }
      GuideApplyingTransmitterFlow.GuideAG4 -> {
        class MediaControllerAG4 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG4.name) }) {
          override val resourceId: Int
            get() = R.raw.ag4_place_on_boilerplate
          override val titleId: Int
            get() = R.string.onboarding_chapter_preparing_place_sl_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_preparing_place_sl_description
          override val position: Int
            get() = 3
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG4())
      }
      GuideApplyingTransmitterFlow.GuideAG5 -> {
        class MediaControllerAG5 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG5.name) }) {
          override val resourceId: Int
            get() = R.raw.ag5_press_down
          override val titleId: Int
            get() = R.string.onboarding_chapter_preparing_press_down_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_preparing_press_down_description
          override val position: Int
            get() = 4
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG5())
      }
      GuideApplyingTransmitterFlow.GuideAG6 -> {
        class MediaControllerAG6 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG6.name) }) {
          override val resourceId: Int
            get() = R.raw.ag6_ensure_secure_adhesion
          override val titleId: Int
            get() = R.string.onboarding_chapter_preparing_secure_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_preparing_secure_description
          override val position: Int
            get() = 5
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG6())
      }
      GuideApplyingTransmitterFlow.GuideAG7 -> {
        class MediaControllerAG7 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG3.name) }) {
          override val resourceId: Int
            get() = R.raw.ag7_remove_sensor_release_liner
          override val titleId: Int
            get() = R.string.onboarding_chapter_applying_remove_sl_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_applying_remove_sl_description
          override val position: Int
            get() = 6
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG7())
      }
      GuideApplyingTransmitterFlow.GuideAG8 -> {
        class MediaControllerAG8 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG8.name) }) {
          override val resourceId: Int
            get() = R.raw.ag8_apply_body
          override val titleId: Int
            get() = R.string.onboarding_chapter_applying_apply_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_applying_apply_description
          override val position: Int
            get() = 7
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG8())
      }
      GuideApplyingTransmitterFlow.GuideAG9 -> {
        class MediaControllerAG9 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG9.name) }) {
          override val resourceId: Int
            get() = R.raw.ag9_lift_connector_flap
          override val titleId: Int
            get() = R.string.onboarding_chapter_applying_connector_flap_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_applying_connector_flap_description
          override val position: Int
            get() = 8
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG9())
      }
      GuideApplyingTransmitterFlow.GuideAG10 -> {
        class MediaControllerAG10 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG10.name) }) {
          override val resourceId: Int
            get() = R.raw.ag10_lift_transmitter_lid
          override val titleId: Int
            get() = R.string.onboarding_chapter_applying_lift_tm_lid_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_applying_lift_tm_lid_description
          override val position: Int
            get() = 9
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG10())
      }
      GuideApplyingTransmitterFlow.GuideAG11 -> {
        class MediaControllerAG11 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG11.name) }) {
          override val resourceId: Int
            get() = R.raw.ag11_identify_dark_grey_side
          override val titleId: Int
            get() = R.string.onboarding_chapter_applying_identify_dark_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_applying_identify_dark_description
          override val position: Int
            get() = 10
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG11())
      }
      GuideApplyingTransmitterFlow.GuideAG12 -> {
        class MediaControllerAG12 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG12.name) }) {
          override val resourceId: Int
            get() = R.raw.ag12_locate_opening
          override val titleId: Int
            get() = R.string.onboarding_chapter_applying_locating_opening_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_applying_locating_opening_description
          override val position: Int
            get() = 11
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG12())
      }
      GuideApplyingTransmitterFlow.GuideAG14 -> {
        class MediaControllerAG14 : MediaGuideController(Bundle().apply { putString(SCREEN_NAME, GuideApplyingTransmitterFlow.GuideAG14.name) }) {
          override val resourceId: Int
            get() = R.raw.ag13_attach_transmitter
          override val titleId: Int
            get() = R.string.onboarding_chapter_applying_attach_title
          override val descriptionId: Int
            get() = R.string.onboarding_chapter_applying_attach_description
          override val position: Int
            get() = 12
          override val totalCount: Int
            get() = 12
        }

        RouterTransaction.with(MediaControllerAG14())
      }
      GuideApplyingTransmitterFlow.AwaitingConnection -> AwaitingConnectionController.routerTransaction()

      GuideApplyingTransmitterFlow.GuideAG15 -> {
        GenericConfirmationController.routerTransaction(
          titleId = R.string.onboarding_chapter_applying_tm_not_attached_title,
          descriptionId = R.string.onboarding_chapter_applying_tm_not_attached_description,
          buttonId = R.string.guide_ag14_2_button,
          imageId = R.drawable.ic_close,
          showCloseButton = true,
          showBackButton = true,
          imageBackgroundDrawable = R.drawable.shape_red_circle,
          imageTint = R.color.confirmation_icon_red
        )
      }

      GuideApplyingTransmitterFlow.SetupCompleted -> {
        GenericConfirmationController.routerTransaction(
          R.string.guide_ap14_title,
          R.string.guide_ap14_subtitle,
          R.string.guide_ap14_button,
          imageId = R.drawable.ic_checkmark,
          showCloseButton = true,
          showBackButton = true
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