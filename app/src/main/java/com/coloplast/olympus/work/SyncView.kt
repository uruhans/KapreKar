package com.coloplast.olympus.work

import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.Observable

interface SyncView : UiView<GenericUiModel> {

  fun beginSync() : Observable<Unit>
}