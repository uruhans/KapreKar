package com.coloplast.olympus.ui.feature.transmitters.models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.models.Battery
import com.coloplast.olympus.shared.models.DiffCallback
import com.coloplast.olympus.shared.models.Transmitter

class TransmitterItem(val transmitter: Transmitter, val onEdit: ObservableBoolean, val listener: Listener): DiffCallback {
  interface Listener {
    fun onDeleteTransmitter(transmitter: Transmitter)
  }

  val battery = ObservableField<Battery>()
  val connectionState = ObservableField<BleConnectionState>()
  var attached: Boolean = false

  fun setBatteryInfo(battery: Battery){
    this.battery.set(battery)
  }
  
  fun setConnectionState(connectionState: BleConnectionState) {
    this.connectionState.set(connectionState)
  }

  fun onItemClicked() {
    listener.onDeleteTransmitter(transmitter)
  }

  override fun areItemsTheSame(other: DiffCallback): Boolean {
    return areContentsTheSame(other)
  }

  override fun areContentsTheSame(other: DiffCallback): Boolean {
    return (other as? TransmitterItem)?.let {
      this.battery.get() == it.battery.get()
          && this.connectionState.get() == it.connectionState.get()
    } ?: false
  }
}