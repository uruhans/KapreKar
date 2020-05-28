package com.coloplast.olympus.ui.feature.transmitters.models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.shared.models.Transmitter

class TransmitterItem(val transmitter: Transmitter, val onEdit: ObservableBoolean, val listener: Listener) {
  interface Listener {
    fun onDeleteTransmitter(transmitter: Transmitter)
  }

  val currentPowerLevel = ObservableField<PowerLevel>()
  val connectionState = ObservableField<BleConnectionState>()
  
  fun setPowerLevel(powerLevel: PowerLevel) {
    currentPowerLevel.set(powerLevel)
  }
  
  fun setConnectionState(connectionState: BleConnectionState) {
    this.connectionState.set(connectionState)
  }
}