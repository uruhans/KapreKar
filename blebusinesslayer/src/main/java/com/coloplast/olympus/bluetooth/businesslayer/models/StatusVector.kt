package com.coloplast.olympus.bluetooth.businesslayer.models

/**
 * A class representing the StatusVector coming from the BleDevice, converted into a StatusVector split into a Patch and a Transmitter
 */
data class StatusVector(
    val patch: Patch,
    val transmitter: Transmitter
)