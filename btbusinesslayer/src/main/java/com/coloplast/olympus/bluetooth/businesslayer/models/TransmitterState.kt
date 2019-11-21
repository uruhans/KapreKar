package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.*

data class TransmitterState(
    val waterIngressDetection: WaterIngressDetection,
    val attached: Attached,
    val patchType: PatchType,
    val compromised: Compromised,
    val rawRotationalAngle: Int,
    val battery: Battery
)