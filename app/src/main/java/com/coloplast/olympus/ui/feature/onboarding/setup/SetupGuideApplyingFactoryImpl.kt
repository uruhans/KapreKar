package com.coloplast.olympus.ui.feature.onboarding.setup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.setup.applying.AwaitingConnectionController
import com.coloplast.olympus.ui.feature.onboarding.setup.applying.SetupGuideApplyingSGA8Controller
import com.coloplast.olympus.ui.feature.onboarding.setup.model.ApplyingFlowModel
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideApplyingFlow
import com.coloplast.olympus.ui.feature.onboarding.setup.model.TransmitterCheckerSelectionType

class SetupGuideApplyingFactoryImpl : FlowFactory<ApplyingFlowModel>() {

  private val model = ApplyingFlowModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    val screenName = SetupGuideApplyingFlow.fromIndex(currentIndex).name

    return when (SetupGuideApplyingFlow.fromIndex(currentIndex)) {
      SetupGuideApplyingFlow.SetupGuideSG5 -> SetupGuideSG5.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideRemoveSlSGA1 -> {
        MediaGuideController.routerTransaction(
          resourceId = R.raw.sg_a1_removeslreleaseliner,
          titleId = R.string.onboarding_chapter_applying_remove_sl_title,
          descriptionId = R.string.onboarding_chapter_applying_remove_sl_description,
          position = 1,
          totalCount = 3,
          screenName = screenName,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideDirectContact -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sensor_layer,
          R.string.onboarding_chapter_applying_contact_to_skin_title,
          R.string.onboarding_chapter_applying_contact_to_skin_description,
          R.string.onboarding_confirmation_button,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideApplySGA2 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a2_applytobody,
        titleId = R.string.onboarding_chapter_applying_apply_title,
        descriptionId = R.string.onboarding_chapter_applying_apply_description,
        position = 2,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuideConnectorFlapSGA3 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a3_liftconnectorflap,
        titleId = R.string.onboarding_chapter_applying_connector_flap_title,
        descriptionId = R.string.onboarding_chapter_applying_connector_flap_description,
        position = 3,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuidePreventDamageSGA4 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sidemenu_transmitters,
          R.string.onboarding_chapter_applying_prevent_damage_title,
          R.string.onboarding_chapter_applying_prevent_damage_description,
          R.string.generic_continue,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideTakeOutSGA5 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a4_takeouttm,
        titleId = R.string.onboarding_chapter_applying_take_out_title,
        descriptionId = R.string.onboarding_chapter_applying_take_out_description,
        position = 1,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuideEnsureClosedSGA6 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a_tmh3_ensuretmclosed,
        titleId = R.string.onboarding_chapter_applying_ensure_closed_title,
        descriptionId = R.string.onboarding_chapter_applying_ensure_closed_description,
        position = 2,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuideLiftLidSGA7 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a_tmh4_lifttmlid,
        titleId = R.string.onboarding_chapter_applying_lift_lid_title,
        descriptionId = R.string.onboarding_chapter_applying_lift_lid_description,
        position = 3,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuideTransmitterCheckerSGA8 -> SetupGuideApplyingSGA8Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideWaitSGATMH1 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_transmitter_nok,
          R.string.onboarding_chapter_applying_wait_title,
          R.string.onboarding_chapter_applying_wait_description,
          R.string.generic_continue,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideNoProblemSGATMH2 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_transmitter,
          R.string.onboarding_chapter_applying_no_problem_title,
          R.string.onboarding_chapter_applying_no_problem_description,
          R.string.generic_continue,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideEnsureClosedSGATMH3 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a_tmh3_ensuretmclosed,
        titleId = R.string.onboarding_chapter_applying_ensure_tm_closed_title,
        descriptionId = R.string.onboarding_chapter_applying_ensure_tm_closed_description,
        position = 1,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuideLiftLidSGATMH4 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a_tmh4_lifttmlid,
        titleId = R.string.onboarding_chapter_applying_lift_tm_lid_title,
        descriptionId = R.string.onboarding_chapter_applying_lift_tm_lid_description,
        position = 2,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuideCheckLidFixedSGATMH5 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a_tmh5_checklidfixed,
        titleId = R.string.onboarding_chapter_applying_check_lid_fixed_title,
        descriptionId = R.string.onboarding_chapter_applying_lift_tm_lid_description,
        position = 3,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuideContinueSGATMH6 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_icon_questionmark_circle,
          R.string.onboarding_chapter_applying_ready_to_continue_title,
          R.string.onboarding_chapter_applying_ready_to_continue_description,
          R.string.onboarding_chapter_applying_ready_to_continue_button_continue,
          R.string.onboarding_chapter_applying_ready_to_continue_button_support,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideReadyAttachSGA9 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_transmitter_ok,
          R.string.onboarding_chapter_applying_ready_attach_title,
          R.string.onboarding_chapter_applying_ready_attach_description,
          R.string.generic_continue,
          showBackButton = false,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideLocateOpeningSGA10 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a6_locatetmopening,
        titleId = R.string.onboarding_chapter_applying_locating_opening_title,
        descriptionId = R.string.onboarding_chapter_applying_locating_opening_description,
        position = 1,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuideIdentifySGA11 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a7_identifyreverse,
        titleId = R.string.onboarding_chapter_applying_identify_dark_title,
        descriptionId = R.string.onboarding_chapter_applying_identify_dark_description,
        position = 2,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.SetupGuideAttachTransmitterSGA12 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_a12_attach_tm_flap,
        titleId = R.string.onboarding_chapter_applying_attach_title,
        descriptionId = R.string.onboarding_chapter_applying_attach_description,
        position = 3,
        totalCount = 3,
        screenName = screenName,
        showCloseButton = false
      )
      SetupGuideApplyingFlow.AwaitingConnection -> AwaitingConnectionController.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideAttachedSuccessSGA13 -> {
        GenericConfirmationController.routerTransaction(
          R.string.onboarding_chapter_applying_attached_title,
          R.string.onboarding_chapter_applying_attached_description,
          R.string.generic_continue,
          imageId = R.drawable.ic_checkmark,
          showCloseButton = false,
          showBackButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideAttachedFailedSGA13_2 -> {
        GenericConfirmationController.routerTransaction(
          R.string.onboarding_chapter_applying_tm_not_attached_title,
          R.string.onboarding_chapter_applying_tm_not_attached_description,
          R.string.onboarding_chapter_applying_tm_not_attached_button_restart,
          negativeButtonId = R.string.onboarding_chapter_applying_tm_not_attached_button_support,
          imageId = R.drawable.ic_close,
          showCloseButton = false,
          imageBackgroundDrawable = R.drawable.shape_red_circle,
          imageTint = R.color.confirmation_icon_red
        )
      }
      SetupGuideApplyingFlow.SetupGuideCompleted -> {
        GenericConfirmationController.routerTransaction(
          R.string.onboarding_complete_title,
          R.string.onboarding_complete_description,
          R.string.generic_continue,
          imageId = R.drawable.ic_checkmark,
          showCloseButton = false,
          showBackButton = false
        )
      }
      SetupGuideApplyingFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    @Suppress("NON_EXHAUSTIVE_WHEN")
    when (SetupGuideApplyingFlow.fromIndex(currentIndex)) {
      SetupGuideApplyingFlow.SetupGuideTransmitterCheckerSGA8 -> {
        model.transmitterCheckerSelectionType = result as TransmitterCheckerSelectionType
      }
      SetupGuideApplyingFlow.SetupGuideContinueSGATMH6 -> {
        model.SetupGuideContinueSGATMH6_Call_Support = (result as Boolean).not()
      }
      SetupGuideApplyingFlow.AwaitingConnection -> {
        model.connectionSuccessful = result as Boolean
      }
      SetupGuideApplyingFlow.SetupGuideAttachedFailedSGA13_2 -> {
        model.SetupGuideAttachedFailedSGA13_2_Call_Support = (result as Boolean).not()
      }
    }
  }

  override fun getData(): ApplyingFlowModel {
    return model
  }
}