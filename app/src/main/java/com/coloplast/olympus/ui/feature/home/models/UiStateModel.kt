package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.ui.feature.statemachine.models.UiState

data class UiStateModel(val uiState: UiState, val bleDevice: BleDevice)