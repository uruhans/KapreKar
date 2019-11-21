package com.coloplast.olympus.bluetooth.businesslayer.models

data class StatusVector(
    val patchState: PatchState,
    val transmitterState: TransmitterState
)