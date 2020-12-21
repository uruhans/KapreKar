package com.coloplast.olympus.bluetooth.businesslayer.models

enum class PatchState(val priority: Int) {
  OK(15),
  INNER_BLIND(14),
  // priority is wrong, but is not used
  INNER_BLIND_LEAK(6),
  TOTAL_BLIND(13),
  PAY_ATTENTION_12(12),
  INNER_LEAK(11),
  PAY_ATTENTION_10(10),
  OUTER_LEAK(9),
  CUTGUARD_DIRT_IN_TRANSMITTER(8),
  CUTGUARD_CUT_BROKEN(7)
}