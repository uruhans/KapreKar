package com.coloplast.olympus.ui.feature.update.charging.model

import com.coloplast.olympus.bluetooth.businesslayer.enums.BatteryState
import com.coloplast.olympus.ui.common.models.UiModel

class ChargingStateModel(val macAddress: String , val batteryState: BatteryState)