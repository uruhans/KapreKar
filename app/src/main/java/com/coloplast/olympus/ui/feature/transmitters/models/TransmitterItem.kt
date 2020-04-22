package com.coloplast.olympus.ui.feature.transmitters.models

import androidx.databinding.ObservableField
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.shared.models.Transmitter

class TransmitterItem(val transmitter: Transmitter, val listener: Listener) {
  interface Listener {
    fun onTransmitterClicked()
  }
  
  val currentPowerLevel = ObservableField<PowerLevel>()
  
  fun setPowerLevel(powerLevel: PowerLevel) {
    currentPowerLevel.set(powerLevel)
  }
}