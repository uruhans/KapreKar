package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.*

data class Status(
    val sensor0: SensorStatus,
    val sensor1: SensorStatus,
    val sensor2: SensorStatus,
    val sensor3: SensorStatus,
    val sensor4: SensorStatus,
    val sensor5: SensorStatus,
    val waterIngressDetection: WaterIngressDetection,
    val attached: Attached,
    val patchType: PatchType,
    val compromised: Compromised,
    val rotationalAngle: Int,
    val battery: Battery
)