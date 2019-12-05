package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID

object CurrentTimeService {
  val UUID = "1805".asUUID()
  
  object Characteristics {
    val CURRENT_TIME = "2a2b".asUUID()
  }
}