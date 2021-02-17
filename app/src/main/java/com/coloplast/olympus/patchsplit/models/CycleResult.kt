package com.coloplast.olympus.patchsplit.models

sealed class CycleResult {
  data class Success(val attachCycleType: AttachCycleType) : CycleResult()
  data class Failure(val attachCycleError: AttachCycleError) : CycleResult()
}