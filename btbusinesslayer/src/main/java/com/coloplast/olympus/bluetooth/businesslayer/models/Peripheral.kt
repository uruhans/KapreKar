package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel

data class Peripheral(val status: Status, val powerLevel: PowerLevel)