package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.SensorStatus

data class PatchState(
    val sensor0: SensorStatus,
    val sensor1: SensorStatus,
    val sensor2: SensorStatus,
    val sensor3: SensorStatus,
    val sensor4: SensorStatus,
    val sensor5: SensorStatus
)