package com.coloplast.olympus.ui.feature.update.charging

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.enums.BatteryState
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.GenericView
import com.coloplast.olympus.ui.feature.update.charging.model.ChargingStateModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class FirmwareUpdateChargingViewModel : BaseViewModel<GenericUiModel,GenericView>() {

  override fun bind(uiView: GenericView): Disposable {

    val disposable = CompositeDisposable()

    val chargingObservable = StateDispatcher.stateChangeObservable()
      .map<GenericUiModel> { stateChange ->
        with(stateChange.bleDevice) {
          GenericUiModel.Success(ChargingStateModel(macAddress , peripheral?.battery?.state ?: BatteryState.UNKNOWN  ))
        }
      }


    disposable.add(chargingObservable.bind(uiView.render()))
    return disposable
  }
}