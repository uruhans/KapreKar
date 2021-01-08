package com.coloplast.olympus.ui.feature.guides.flows.applying

import com.coloplast.olympus.shared.types.TypeWithId

enum class GuideApplyingTransmitterFlow(override val id: Int) : TypeWithId {
  GuideAG1(0),
  GuideAG2(1),
  GuideAG2_5(2),
  GuideAG3(3),
  GuideAG4(4),
  GuideAG5(5),
  GuideAG6(6),
  GuideAG7(7),
  GuideAG8(8),
  GuideAG9(9),
  GuideAG10(10),
  GuideAG11(11),
  GuideAG12(12),
  GuideAG14(13),
  AwaitingConnection(14),
  GuideAG15(15),
  SetupCompleted(16),
  Completed(17);

  companion object {
    fun fromIndex(id: Int): GuideApplyingTransmitterFlow {
      return values().associateBy { item: GuideApplyingTransmitterFlow -> item.id }[id] ?: Completed
    }
  }
}