package com.coloplast.olympus.ui.feature.transmitters.models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.Battery
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.models.DiffListItem
import com.coloplast.olympus.shared.models.Transmitter

class TransmitterItem(val transmitter: Transmitter, val onEdit: ObservableBoolean, val listener: Listener): DiffListItem {
  interface Listener {
    fun onDeleteTransmitter(transmitter: Transmitter)
  }

  val battery = ObservableField<Battery>()
  val connectionState = ObservableField<BleConnectionState>()
  val attached = ObservableBoolean(false)
  val detectable = ObservableBoolean(false)

  fun setStateChange(stateChange: StateChange){
    this.battery.set(stateChange.bleDevice.peripheral?.battery)
    attached.set(stateChange.bleDevice.isAttached())
    detectable.set(stateChange.bleDevice.isConnected())
  }

  fun setConnectionState(connectionState: BleConnectionState){
    this.connectionState.set(connectionState)
    detectable.set(connectionState == BleConnectionState.CONNECTED)
  }

  override fun areItemsTheSame(other: DiffListItem): Boolean {
    return (other as? TransmitterItem)?.let {
      this.transmitter.id == it.transmitter.id
    } ?: false
  }

  override fun areContentsTheSame(other: DiffListItem): Boolean {
    return (other as? TransmitterItem)?.let {
      this.battery.get() == other.battery.get()
          && this.connectionState.get() == other.connectionState.get()
          && this.attached.get() == other.attached.get()
          && this.detectable.get() == other.detectable.get()
    } ?: false
  }
}