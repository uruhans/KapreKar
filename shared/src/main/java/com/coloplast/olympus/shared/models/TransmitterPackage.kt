package com.coloplast.olympus.shared.models

import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.shared.types.SyncState
import java.time.Instant
import java.util.*

class TransmitterPackage(
  val id: Int? = null,
  val transmitterId: String,
  var deviceId: UUID? = null,
  var patchId: Int? = null,
  val timestamp: Instant = Instant.now(),
  val packageType: PackageType,
  val value: ByteArray,
  val syncState: SyncState = SyncState.Pending
)