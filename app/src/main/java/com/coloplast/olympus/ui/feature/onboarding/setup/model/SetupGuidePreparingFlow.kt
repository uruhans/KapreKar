package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.types.TypeWithId


enum class SetupGuidePreparingFlow(override val id: Int) : TypeWithId {
  SetupGuideSG4(0),
  SetupGuideChangingSGP1(1),
  SetupGuideComeBackLaterSGP1_2(2),
  SetupGuideDirectContact(3),
  SetupGuideRemoveSGP2(4),
  SetupGuidePlaceSLSGP3(5),
  SetupGuidePressDownSGP4(6),
  SetupGuideSecureSGP5(7),
  Completed(8);

  companion object {
    fun fromIndex(id: Int): SetupGuidePreparingFlow {
      return values().associateBy { item: SetupGuidePreparingFlow -> item.id }[id] ?: Completed
    }
  }
}