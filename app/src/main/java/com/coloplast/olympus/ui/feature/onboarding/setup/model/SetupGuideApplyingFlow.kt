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
  SetupGuideCheckLidFixedSGATMH5(13),
  SetupGuideContinueSGATMH6(14),
  SetupGuideReadyAttachSGA9(15),
  SetupGuideLocateOpeningSGA10(16),
  SetupGuideIdentifySGA11(17),
  SetupGuideAttachTransmitterSGA12(18),
  AwaitingConnection(19),
  SetupGuideAttachedSuccessSGA13(20),
  SetupGuideAttachedFailedSGA13_2(21),
  Completed(22);

  companion object {
    fun fromIndex(id: Int): SetupGuideApplyingFlow {
      return values().associateBy { item: SetupGuideApplyingFlow -> item.id }[id] ?: Completed
    }
  }
}