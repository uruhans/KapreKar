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
  SetupGuideOpeningLidSGI12(9),
  SetupGuideChargerSGI13(10),
  SetupGuidePairingSGI14(11),
  Completed(12);

  companion object {
    fun fromIndex(id: Int): SetupGuideIntroductionFlow {
      return values().associateBy { item: SetupGuideIntroductionFlow -> item.id }[id] ?: Completed
    }
  }
}