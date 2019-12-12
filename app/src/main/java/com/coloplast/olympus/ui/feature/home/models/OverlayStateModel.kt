package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState

data class OverlayStateModel(val overlayState: OverlayState, val bleDevice: BleDevice)