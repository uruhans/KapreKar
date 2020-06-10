package com.coloplast.olympus.bluetooth.businesslayer.models

import com.coloplast.olympus.bluetooth.businesslayer.enums.BatteryState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 27/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
data class Battery(val state: BatteryState = BatteryState.UNKNOWN, val powerLevel: PowerLevel = PowerLevel.EMPTY)