package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.types.TypeWithId

enum class SetupGuideApplyingFlow(override val id: Int) : TypeWithId {
  SetupGuideSG5(0),
  SetupGuideRemoveSlSGA1(1),
  SetupGuideDirectContact(2),
  SetupGuideApplySGA2(3),
  SetupGuideConnectorFlapSGA3(4),
  SetupGuidePreventDamageSGA4(5),
  SetupGuideTakeOutSGA5(6),
  SetupGuideEnsureClosedSGA6(7),
  SetupGuideLiftLidSGA7(8),
  SetupGuideTransmitterCheckerSGA8(9),
  SetupGuideWaitSGATMH1(10),
  SetupGuideNoProblemSGATMH2(11),
  SetupGuideEnsureClosedSGATMH3(12),
  SetupGuideLiftLidSGATMH4(13),
  SetupGuideCheckLidFixedSGATMH5(14),
  SetupGuideContinueSGATMH6(15),
  SetupGuideReadyAttachSGA9(16),
  SetupGuideLocateOpeningSGA10(17),
  SetupGuideIdentifySGA11(18),
  SetupGuideAttachTransmitterSGA12(19),
  AwaitingConnection(20),
  SetupGuideAttachedSuccessSGA13(21),
  SetupGuideAttachedFailedSGA13_2(22),
  SetupGuideCompleted(23),
  Completed(24);

  companion object {
    fun fromIndex(id: Int): SetupGuideApplyingFlow {
      return values().associateBy { item: SetupGuideApplyingFlow -> item.id }[id] ?: Completed
    }
  }
}