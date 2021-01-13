package com.coloplast.olympus.shared.models

import com.coloplast.olympus.shared.types.SyncState
import java.time.Instant

class AppStatePackage(
  val id: Int? = null,
  val appState: String,
  val internalAppState: String,
  val timestamp: Instant = Instant.now(),
  val syncState: SyncState = SyncState.Pending
)