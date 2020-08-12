package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.shared.types.TypeWithId

enum class SetupGuideConnectionFlow(override val id: Int) : TypeWithId {
  SetupGuideSG3(0),
  SetupGuideBluetoothSGC1(1),
  SetupGuideLocationSGC3(2),
  SetupGuideLocationAllowSGC4(3),
  SetupGuideFindTMCodeSGC5(4),
  Completed(5);

  companion object {
    fun fromIndex(id: Int): SetupGuideConnectionFlow {
      return values().associateBy { item: SetupGuideConnectionFlow -> item.id }[id] ?: Completed
    }
  }
}