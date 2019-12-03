package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID

object GenericAccess {
  val UUID = "1800"
  
  object Characteristics {
    val NAME = "2a00".asUUID()
    val APPEARANCE = "2a01".asUUID()
  }
}