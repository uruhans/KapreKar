package com.coloplast.olympus.bluetooth.businesslayer.models

enum class PatchState {
  OK,
  INNER_BLIND,
  TOTAL_BLIND,
  INNER_LEAK,
  OUTER_LEAK,
  PAY_ATTENTION,
  CUTGUARD_DIRT_IN_TRANSMITTER,
  CUTGUARD_CUT_BROKEN
}