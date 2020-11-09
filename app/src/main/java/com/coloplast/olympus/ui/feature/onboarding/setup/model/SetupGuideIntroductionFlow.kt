package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.types.TypeWithId

enum class SetupGuideIntroductionFlow(override val id: Int) : TypeWithId {
  SetupGuideSG1(0),
  SetupGuideSG2(1),
  SetupGuideSGI1(2),
  SetupGuideInTheBoxSGI6(3),
  SetupGuideSensorLayerSGI7(4),
  SetupGuideSensorTechSGI8(5),
  SetupGuideOuterSensorsSGI9(6),
  SetupGuideConnectorFlapSGI10(7),
  SetupGuideTransmitterSGI11(8),
  SetupGuideLiftingTransmitterLidSGI12(9),
  SetupGuideOpeningTransmitterLidSGI13(10),
  SetupGuideDisengagingTransmitterLidSGI14(11),
  SetupGuideClosingTransmitterLidSGI15(12),
  SetupGuideChargerSGI16(13),
  SetupGuidePairingSGI17(14),
  Completed(15);

  companion object {
    fun fromIndex(id: Int): SetupGuideIntroductionFlow {
      return values().associateBy { item: SetupGuideIntroductionFlow -> item.id }[id] ?: Completed
    }
  }
}