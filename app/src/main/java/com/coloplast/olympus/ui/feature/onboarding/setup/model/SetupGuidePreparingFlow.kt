package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.types.TypeWithId


enum class SetupGuidePreparingFlow(override val id: Int) : TypeWithId {
  SetupGuideSG4(0),
  SetupGuideChangingSGP1(1),
  SetupGuideComeBackLaterSGP1_2(2),
  SetupGuideRemoveSGP2(3),
  SetupGuidePlaceSLSGP3(4),
  SetupGuidePressDownSGP4(5),
  SetupGuideSecureSGP5(6),
  Completed(7);

  companion object {
    fun fromIndex(id: Int): SetupGuidePreparingFlow {
      return values().associateBy { item: SetupGuidePreparingFlow -> item.id }[id] ?: Completed
    }
  }
}