package com.coloplast.olympus.ui.feature.guides.flows.applying

import com.coloplast.olympus.shared.types.TypeWithId

enum class GuideApplyingTransmitterFlow(override val id: Int) : TypeWithId {
  GuideAG1(0),
  GuideAG2(1),
  GuideAG3(2),
  GuideAG4(3),
  GuideAG5(4),
  GuideAG6(5),
  GuideAG7(6),
  GuideAG8(7),
  GuideAG9(8),
  GuideAG10(9),
  GuideAG11(10),
  GuideAG12(11),
  GuideAG14(12),
  AwaitingConnection(13),
  GuideAG15(14),
  SetupCompleted(15),
  Completed(16);

  companion object {
    fun fromIndex(id: Int): GuideApplyingTransmitterFlow {
      return values().associateBy { item: GuideApplyingTransmitterFlow -> item.id }[id] ?: Completed
    }
  }
}