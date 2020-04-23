package com.coloplast.olympus.bluetooth.model

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState

class ConnectionState(val macAddress: String, val connectionState: BleConnectionState)