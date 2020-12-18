package com.coloplast.olympus.bluetooth.model

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.shared.models.Transmitter

class TxModel(val transmitter: Transmitter, val connectionState: BleConnectionState)