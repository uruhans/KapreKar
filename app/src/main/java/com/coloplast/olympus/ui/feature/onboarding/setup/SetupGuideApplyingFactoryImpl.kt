package com.coloplast.olympus.ui.feature.onboarding.setup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.flow.EmptyModel
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.setup.applying.*
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideApplyingFlow

class SetupGuideApplyingFactoryImpl : FlowFactory<EmptyModel>() {

  private val model = EmptyModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when(SetupGuideApplyingFlow.fromIndex(currentIndex)) {
      SetupGuideApplyingFlow.SetupGuideSG5 -> SetupGuideSG5.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideRemoveSlSGA1 -> SetupGuideApplyingSGA1Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideApplySGA2 -> SetupGuideApplyingSGA2Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideConnectorFlapSGA3 -> SetupGuideApplyingSGA3Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuidePreventDamageSGA4 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sidemenu_transmitters,
          R.string.onboarding_chapter_applying_prevent_damage_title,
          R.string.onboarding_chapter_applying_prevent_damage_description,
          R.string.generic_continue
        )
      }
      SetupGuideApplyingFlow.SetupGuideTakeOutSGA5 -> SetupGuideApplyingSGA5Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideEnsureClosedSGA6 -> SetupGuideApplyingSGA6Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideLiftLidSGA7 -> SetupGuideApplyingSGA7Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideTransmitterCheckerSGA8 -> TODO()
      SetupGuideApplyingFlow.SetupGuideWaitSGATMH1 -> TODO()
      SetupGuideApplyingFlow.SetupGuideNoProblemSGATMH2 -> TODO()
      SetupGuideApplyingFlow.SetupGuideEnsureClosedSGATMH3 -> SetupGuideApplyingSGA6Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideLiftLidSGATMH4 -> SetupGuideApplyingSGA7Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideCheckLidFixedSGATMH5 -> TODO()
      SetupGuideApplyingFlow.SetupGuideContinueSGATMH6 -> TODO()
      SetupGuideApplyingFlow.SetupGuideReadyAttachSGA9 -> {
        InfoPageController.routerTransaction(
          R.drawable.ic_sidemenu_transmitters, // TODO - correct image
          R.string.onboarding_chapter_applying_ready_attach_title,
          R.string.onboarding_chapter_applying_ready_attach_description,
          R.string.generic_continue
        )
      }
      SetupGuideApplyingFlow.SetupGuideLocateOpeningSGA10 -> SetupGuideApplyingSGA10Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideIdentifySGA11 -> SetupGuideApplyingSGA11Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideAttachTransmitterSGA12 -> SetupGuideApplyingSGA12Controller.routerTransaction()
      SetupGuideApplyingFlow.SetupGuideAttachedSuccessSGA13 -> TODO()
      SetupGuideApplyingFlow.SetupGuideAttachedFailedSGA13_2 -> TODO()
      SetupGuideApplyingFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    /* no-op */
  }

  override fun getData(): EmptyModel {
    return model
  }
}