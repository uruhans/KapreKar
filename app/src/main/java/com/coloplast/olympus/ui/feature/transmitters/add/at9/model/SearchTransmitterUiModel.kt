package com.coloplast.olympus.ui.feature.transmitters.add.at9.model

import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.ui.common.models.UiModel

sealed class SearchTransmitterUiModel : UiModel {
  data class TransmittersFound(val scanResults: List<ScanResult>) : SearchTransmitterUiModel()
  data class PendingUser(val waiting: Boolean = true) : SearchTransmitterUiModel()
  data class NothingFound(val nothingFound: Boolean = true) : SearchTransmitterUiModel()

  data class ConnectionSuccessful(val macAddress: String) : SearchTransmitterUiModel()
  data class ConnectionFailure(val macAddress: String) : SearchTransmitterUiModel()
}