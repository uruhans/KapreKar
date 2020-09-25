package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID

object GenericAttribute {
  val UUID = "1801".asUUID()

  object Characteristics {
    val SERVICE_CHANGED = "2a05".asUUID()
  }
}