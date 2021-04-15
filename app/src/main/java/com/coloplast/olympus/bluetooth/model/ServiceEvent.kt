package com.coloplast.olympus.bluetooth.model

sealed class ServiceEvent {
  data class RetryConnection(val retry: Unit = Unit) : ServiceEvent()
  data class ReconnectionAttempt(val shouldReconnect: Boolean) : ServiceEvent()
  data class DisconnectTransmitter(val macAddress: String) : ServiceEvent()

  data class AppStateLogger(val letter: String) : ServiceEvent()
}