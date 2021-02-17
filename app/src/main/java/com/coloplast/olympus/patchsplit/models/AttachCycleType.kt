package com.coloplast.olympus.patchsplit.models

sealed class AttachCycleType {
  data class ComingFromMultipleTransmitters(val compromised: Int?) : AttachCycleType()
  data class OutOfRange(val compromised: Int?) : AttachCycleType()
  data class Standard(val compromised: Int?) : AttachCycleType()
}