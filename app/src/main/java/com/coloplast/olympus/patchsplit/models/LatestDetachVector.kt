package com.coloplast.olympus.patchsplit.models

import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.moshi.MoshiSerializable
import java.time.Instant

class LatestDetachVector(val stateChange: StateChange, private val timestamp: Long) : MoshiSerializable {
  fun isValid() : Boolean {
    val validTimeStamp = Instant.now().epochSecond.minus(60.times(60))
    return timestamp > validTimeStamp
  }
}