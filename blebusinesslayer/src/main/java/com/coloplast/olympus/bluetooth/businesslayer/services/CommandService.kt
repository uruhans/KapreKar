package com.coloplast.olympus.bluetooth.businesslayer.services

import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID

object CommandService {
  val UUID = "0003CDD0-0000-1000-8000-00805F9B0131".asUUID()

  object Characteristics {
    val UART_RX = "0003CDD2-0000-1000-8000-00805F9B0131".asUUID()
    val UART_TX = "0003CDD1-0000-1000-8000-00805F9B0131".asUUID()
  }
}