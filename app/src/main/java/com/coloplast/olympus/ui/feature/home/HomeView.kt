package com.coloplast.olympus.ui.feature.home

import com.coloplast.olympus.ui.common.models.UiView
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import io.reactivex.Observable

interface HomeView : UiView<HomeUiModel> {
  fun requestConnectionState() : Observable<String>
  fun requestStitching() : Observable<Pair<String, ByteArray>> // macAddress, stitch array
  fun requestReset() : Observable<String> // macAddress
}