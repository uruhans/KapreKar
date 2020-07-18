package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.ui.common.TypeWithId

enum class SetupGuideApplyingFlow(override val id: Int) : TypeWithId {
  SetupGuideSG5(0),
  SetupGuideRemoveSlSGA1(1),
  SetupGuideApplySGA2(2),
  SetupGuideConnectorFlapSGA3(3),
  SetupGuidePreventDamageSGA4(4),
  SetupGuideTakeOutSGA5(5),
  SetupGuideEnsureClosedSGA6(6),
  SetupGuideLiftLidSGA7(7),
  SetupGuideTransmitterCheckerSGA8(8),
  SetupGuideWaitSGATMH1(9),
  SetupGuideNoProblemSGATMH2(10),
  SetupGuideEnsureClosedSGATMH3(11),
  SetupGuideLiftLidSGATMH4(12),
  SetupGuideCheckLidFixedSGATMH5(12),
  SetupGuideContinueSGATMH6(13),
  SetupGuideReadyAttachSGA9(14),
  SetupGuideLocateOpeningSGA10(15),
  SetupGuideIdentifySGA11(16),
  SetupGuideAttachTransmitterSGA12(17),
  SetupGuideAttachedSuccessSGA13(18),
  SetupGuideAttachedFailedSGA13_2(19),
  Completed(20);

  companion object {
    fun fromIndex(id: Int): SetupGuideApplyingFlow {
      return values().associateBy { item: SetupGuideApplyingFlow -> item.id }[id] ?: Completed
    }
  }
}