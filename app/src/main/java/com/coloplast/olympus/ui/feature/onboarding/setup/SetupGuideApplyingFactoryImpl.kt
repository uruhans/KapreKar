package com.coloplast.olympus.ui.feature.onboarding.setup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.setup.applying.*
import com.coloplast.olympus.ui.feature.onboarding.setup.model.ApplyingFlowModel
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideApplyingFlow
import com.coloplast.olympus.ui.feature.onboarding.setup.model.TransmitterCheckerSelectionType

class SetupGuideApplyingFactoryImpl : FlowFactory<ApplyingFlowModel>() {

  private val model = ApplyingFlowModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (SetupGuideApplyingFlow.fromIndex(currentIndex)) {
      SetupGuideApplyingFlow.SetupGuideSG5 -> SetupGuideSG5.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideRemoveSlSGA1 -> SetupGuideApplyingSGA1Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideApplySGA2 -> SetupGuideApplyingSGA2Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideConnectorFlapSGA3 -> SetupGuideApplyingSGA3Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuidePreventDamageSGA4 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sidemenu_transmitters,
          R.string.onboarding_chapter_applying_prevent_damage_title,
          R.string.onboarding_chapter_applying_prevent_damage_description,
          R.string.generic_continue,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideTakeOutSGA5 -> SetupGuideApplyingSGA5Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideEnsureClosedSGA6 -> SetupGuideApplyingSGA6Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideLiftLidSGA7 -> SetupGuideApplyingSGA7Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideTransmitterCheckerSGA8 -> SetupGuideApplyingSGA8Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideWaitSGATMH1 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sidemenu_transmitters,
          R.string.onboarding_chapter_applying_wait_title,
          R.string.onboarding_chapter_applying_wait_description,
          R.string.generic_continue,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideNoProblemSGATMH2 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sidemenu_transmitters,
          R.string.onboarding_chapter_applying_no_problem_title,
          R.string.onboarding_chapter_applying_no_problem_description,
          R.string.generic_continue,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideEnsureClosedSGATMH3 -> SetupGuideApplyingSGATMH3Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideLiftLidSGATMH4 -> SetupGuideApplyingSGATMH4Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideCheckLidFixedSGATMH5 -> SetupGuideApplyingSGATMH5Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideContinueSGATMH6 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sidemenu_transmitters, // todo - correct image
          R.string.onboarding_chapter_applying_ready_to_continue_title,
          R.string.onboarding_chapter_applying_ready_to_continue_description,
          R.string.onboarding_chapter_applying_ready_to_continue_button_continue,
          R.string.onboarding_chapter_applying_ready_to_continue_button_support,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideReadyAttachSGA9 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sidemenu_transmitters, // TODO - correct image
          R.string.onboarding_chapter_applying_ready_attach_title,
          R.string.onboarding_chapter_applying_ready_attach_description,
          R.string.generic_continue,
          showCloseButton = false
        )
      }
      SetupGuideApplyingFlow.SetupGuideLocateOpeningSGA10 -> SetupGuideApplyingSGA10Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideIdentifySGA11 -> SetupGuideApplyingSGA11Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideAttachTransmitterSGA12 -> SetupGuideApplyingSGA12Controller.routerTransaction()
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
    when (SetupGuideApplyingFlow.fromIndex(currentIndex)) {
      SetupGuideApplyingFlow.SetupGuideTransmitterCheckerSGA8 -> {
        when (result as TransmitterCheckerSelectionType) {
          TransmitterCheckerSelectionType.FIXED -> skipUntil(SetupGuideApplyingFlow.SetupGuideReadyAttachSGA9.id)
          TransmitterCheckerSelectionType.LOOSE -> skipUntil(SetupGuideApplyingFlow.SetupGuideWaitSGATMH1.id)
          TransmitterCheckerSelectionType.NOT_SURE -> skipUntil(SetupGuideApplyingFlow.SetupGuideNoProblemSGATMH2.id)
        }
      }
      SetupGuideApplyingFlow.AwaitingConnection -> {
        model.connectionSuccessful = result as Boolean
      }
      SetupGuideApplyingFlow.SetupGuideContinueSGATMH6 -> {
        model.SetupGuideContinueSGATMH6_Call_Support = (result as Boolean).not()
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