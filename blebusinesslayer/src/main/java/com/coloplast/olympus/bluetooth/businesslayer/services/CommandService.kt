package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID

object CommandService {
  val UUID = "0003cdd0-0000-1000-8000-00805f9b0131".asUUID()

  object Characteristics {
    val UART_RX = "0003cdd2-0000-1000-8000-00805f9b0131".asUUID()
    val UART_TX = "0003cdd1-0000-1000-8000-00805f9b0131".asUUID()
  }
}