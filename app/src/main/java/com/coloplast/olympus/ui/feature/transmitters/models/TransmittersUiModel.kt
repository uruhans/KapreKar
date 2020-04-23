package com.coloplast.olympus.ui.feature.transmitters.models

import com.coloplast.olympus.bluetooth.model.ConnectionState
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.models.UiModel

sealed class TransmittersUiModel : UiModel {

  data class ConnectedDevices(val devices: List<Transmitter>) : TransmittersUiModel()
  data class Empty(val empty: Boolean = true): TransmittersUiModel()
  data class StateUpdate(val stateChange: StateChange) : TransmittersUiModel()
  data class TransmitterConnectionState(val connectionState: ConnectionState) : TransmittersUiModel()
  
}