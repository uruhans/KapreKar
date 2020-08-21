package com.coloplast.olympus.shared.types

enum class SyncState(val id: Int) {
  Pending(0),
  Sending(1),
  Uploaded(2);

  companion object {
    fun fromId(id: Int): SyncState {
      return values().first { it.id == id }
    }
  }
}