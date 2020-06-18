package com.coloplast.olympus.bluetooth.model

sealed class ServiceEvent {
  data class RetryConnection(val retry: Unit = Unit) : ServiceEvent()
  data class DisconnectTransmitter(val macAddress: String) : ServiceEvent()
}