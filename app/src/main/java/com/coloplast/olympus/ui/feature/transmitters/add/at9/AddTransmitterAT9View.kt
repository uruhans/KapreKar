package com.coloplast.olympus.ui.feature.transmitters.add.at9

import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.ui.common.models.UiView
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel
import io.reactivex.Observable

interface AddTransmitterAT9View : UiView<SearchTransmitterUiModel> {
  fun retryTransmitterSearchObservable() : Observable<Unit>
  val transmitterToSearchFor : String
  fun scanResultToConnect() : Observable<ScanResult>
}