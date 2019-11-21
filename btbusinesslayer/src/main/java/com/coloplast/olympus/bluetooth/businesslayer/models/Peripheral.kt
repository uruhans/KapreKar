package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel

data class Peripheral(val patchState: PatchState, val transmitterState: TransmitterState, val powerLevel: PowerLevel)