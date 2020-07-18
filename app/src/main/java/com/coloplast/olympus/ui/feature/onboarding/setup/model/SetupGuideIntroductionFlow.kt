package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.ui.common.TypeWithId

enum class SetupGuideIntroductionFlow(override val id: Int) : TypeWithId {
  SetupGuideSG1(0),
  SetupGuideSG2(1),
  SetupGuideSGI1(2),
  SetupGuideBluetoothSGI2(3),
  SetupGuideInTheBoxSGI6(4),
  SetupGuideSensorLayerSGI7(5),
  SetupGuideSensorTechSGI8(6),
  SetupGuideOuterSensorsSGI9(7),
  SetupGuideConnectorFlapSGI10(8),
  SetupGuideTransmitterSGI11(9),
  SetupGuideOpeningLidSGI12(10),
  SetupGuideChargerSGI13(11),
  SetupGuidePairingSGI14(12),
  Completed(13);

  companion object {
    fun fromIndex(id: Int): SetupGuideIntroductionFlow {
      return values().associateBy { item: SetupGuideIntroductionFlow -> item.id }[id] ?: Completed
    }
  }
}