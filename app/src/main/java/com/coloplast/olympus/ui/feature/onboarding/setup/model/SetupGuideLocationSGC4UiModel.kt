package com.coloplast.olympus.ui.feature.onboarding.setup.model

import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.ui.common.models.UiModel

sealed class SetupGuideLocationSGC4UiModel : UiModel {
  data class CurrentBluetoothState(val state: BluetoothState) : SetupGuideLocationSGC4UiModel()
}