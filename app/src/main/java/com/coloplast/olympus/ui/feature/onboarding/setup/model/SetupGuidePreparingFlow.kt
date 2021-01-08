package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.types.TypeWithId


enum class SetupGuidePreparingFlow(override val id: Int) : TypeWithId {
  SetupGuideSG4(0),
  SetupGuideChangingSGP1(1),
  SetupGuideComeBackLaterSGP1_2(2),
  SetupGuideDirectContact(3),
  SetupGuideIdentifySLSGP21(4),
  SetupGuideSelectSensorLayerSize(5),
  SetupGuideLiftFlapSGP23(6),
  SetupGuideRemoveSGP2(7),
  SetupGuidePlaceSLSGP3(8),
  SetupGuidePressDownSGP4(9),
  SetupGuideSecureSGP5(10),
  Completed(11);

  companion object {
    fun fromIndex(id: Int): SetupGuidePreparingFlow {
      return values().associateBy { item: SetupGuidePreparingFlow -> item.id }[id] ?: Completed
    }
  }
}