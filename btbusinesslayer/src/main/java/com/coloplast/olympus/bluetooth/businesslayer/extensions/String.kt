package com.coloplast.olympus.bluetooth.businesslayer.extensions

import java.util.*

fun String.asUUID(): UUID {
  if (this.length > 4) {
    return UUID.fromString(this)
  }
  return UUID.fromString("0000$this-0000-1000-8000-00805f9b34fb")
}