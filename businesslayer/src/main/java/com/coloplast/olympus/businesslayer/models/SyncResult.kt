package com.coloplast.olympus.businesslayer.models

sealed class SyncResult {
  data class Completed(val tuplesRemaining: Int = 0) : SyncResult()

  data class Error(val state: State = State.SYNC_FAILED) : SyncResult() {
    enum class State {
      DELETE_FAILED,
      SYNC_FAILED
    }
  }
}