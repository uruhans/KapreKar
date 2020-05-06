package com.coloplast.olympus.ui.feature.transmitters.add.at9

import com.coloplast.olympus.ui.common.models.UiView
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel

interface AddTransmitterAT9View : UiView<SearchTransmitterUiModel> {
  fun getTransmitterName(): String
}