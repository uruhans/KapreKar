package com.coloplast.olympus.ui.feature.transmitters.add

import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.Observable

interface AddTransmitterView : UiView<GenericUiModel> {
  fun onScanRequested() : Observable<Unit>
  fun onAddTransmitter() : Observable<ScanResult>
}