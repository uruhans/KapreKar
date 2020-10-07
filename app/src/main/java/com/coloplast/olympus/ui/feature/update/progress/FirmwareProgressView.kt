package com.coloplast.olympus.ui.feature.update.progress

import com.coloplast.olympus.ui.common.models.GenericView
import com.coloplast.olympus.ui.feature.update.progress.model.UpdateModel
import io.reactivex.Observable

interface FirmwareProgressView : GenericView {
  fun updateFirmwareRequested() : Observable<UpdateModel>
}