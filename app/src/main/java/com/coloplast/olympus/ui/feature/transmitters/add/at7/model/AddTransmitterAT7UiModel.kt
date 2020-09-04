package com.coloplast.olympus.ui.feature.transmitters.add.at7.model

import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.ui.common.models.UiModel

data class AddTransmitterAT7UiModel(val bluetoothState: BluetoothState) : UiModel