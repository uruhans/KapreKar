package com.coloplast.olympus.patchsplit

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.Peripheral

data class MockBleDevice(val name: String = "Mock", val macAddress: String = "AA:BB:CC:DD:EE:FF", var connectionState: BleConnectionState = BleConnectionState.CONNECTED, var peripheral: Peripheral? = null)
