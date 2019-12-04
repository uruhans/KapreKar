package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.bluetooth.businesslayer.models.BluetoothState
import com.coloplast.olympus.ui.common.models.UiModel

sealed class HomeUiModel : UiModel {

  data class Connected(val connected: Boolean = true) : HomeUiModel()
  data class BluetoothIssue(val bluetoothState: BluetoothState) : HomeUiModel()
  
}