package com.coloplast.olympus.bluetooth.businesslayer.extensions

import java.util.*

/**
 * a String extention specifically for Android
 * It's used because the Characteristics Specification use a short uuid, but Android *has* to use the full sized UUID
 */
fun String.asUUID(): UUID {
  if (this.length > 4) {
    return UUID.fromString(this)
  }
  return UUID.fromString("0000$this-0000-1000-8000-00805f9b34fb")
}