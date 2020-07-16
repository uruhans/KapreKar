package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.ui.common.TypeWithId

enum class SetupGuideIntroductionFlow(override val id: Int) : TypeWithId {
  SetupGuideSG1(0),
  SetupGuideSG2(1),
  SetupGuideSGI1(2),
  SetupGuideBluetoothSGI2(3),
  Completed(4);

  companion object {
    fun fromIndex(id: Int): SetupGuideIntroductionFlow {
      return values().associateBy { item: SetupGuideIntroductionFlow -> item.id }[id] ?: Completed
    }
  }
}