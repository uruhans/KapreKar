package com.coloplast.olympus.ui.feature.transmitters

import com.coloplast.olympus.ui.common.models.UiView
import com.coloplast.olympus.ui.feature.transmitters.models.TransmittersUiModel
import io.reactivex.Observable

interface TransmittersView : UiView<TransmittersUiModel> {
  fun requestDeleteTransmitter() : Observable<String>
  fun retryConnection() : Observable<Unit>
}