package com.coloplast.olympus.shared.models

import com.coloplast.olympus.shared.types.SyncState
import java.util.*

class Transmitter(
  val id: String,
  val userId: UUID,
  var name: String,
  var nickName: String,
  var firmwareVersion: String? = null,
  var serialNumber: String? = null,
  var hardwareRevision: String? = null,
  var manufacturerName: String? = null,
  var settings: ByteArray? = null,
  var classifierSettings: ByteArray? = null,
  var syncState: SyncState = SyncState.Pending
)