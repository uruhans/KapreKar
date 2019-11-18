package com.coloplast.olympus.bluetooth.businesslayer.utils

import java.util.*

object UUIDHelper {
  
  fun asUUID(uuid: String): UUID {
    if (uuid.length > 4) {
      return UUID.fromString(uuid)
    }
    return UUID.fromString("0000$uuid-0000-1000-8000-00805f9b34fb")
  }
}