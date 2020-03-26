package com.coloplast.olympus.ui.feature.transmitters.models

import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.ui.common.models.UiModel

sealed class TransmittersUiModel : UiModel {

  data class ConnectedDevices(val devices: List<BleDevice>) : TransmittersUiModel()

}