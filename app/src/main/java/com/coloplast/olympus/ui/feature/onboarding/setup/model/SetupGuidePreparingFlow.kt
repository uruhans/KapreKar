package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.types.TypeWithId


enum class SetupGuidePreparingFlow(override val id: Int) : TypeWithId {
  SetupGuideSG4(0),
  SetupGuideChangingSGP1(1),
  SetupGuideComeBackLaterSGP1_2(2),
  SetupGuideDirectContact(3),
  SetupGuideSelectSensorLayerSize(4),
  SetupGuideRemoveSGP2(5),
  SetupGuidePlaceSLSGP3(6),
  SetupGuidePressDownSGP4(7),
  SetupGuideSecureSGP5(8),
  Completed(9);

  companion object {
    fun fromIndex(id: Int): SetupGuidePreparingFlow {
      return values().associateBy { item: SetupGuidePreparingFlow -> item.id }[id] ?: Completed
    }
  }
}