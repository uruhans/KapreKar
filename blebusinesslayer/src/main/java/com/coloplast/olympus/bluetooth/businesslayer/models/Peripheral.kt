package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel

/**
 * Once we are connected the Peripheral contains the actual data from the BleDevice
 */
data class Peripheral(val patch: Patch, val transmitter: Transmitter, val powerLevel: PowerLevel)