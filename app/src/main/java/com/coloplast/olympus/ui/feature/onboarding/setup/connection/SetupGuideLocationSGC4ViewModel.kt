package com.coloplast.olympus.ui.feature.onboarding.setup.connection

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideLocationSGC4UiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class SetupGuideLocationSGC4ViewModel(private val bluetoothManager: BluetoothManager) :
  BaseViewModel<SetupGuideLocationSGC4UiModel, SetupGuideLocationSGC4View>() {
  override fun bind(uiView: SetupGuideLocationSGC4View): Disposable {
    val disposable = CompositeDisposable()

    val bluetoothStateObservable: Observable<SetupGuideLocationSGC4UiModel> =
      bluetoothManager.bluetoothStateObservable()
        .map<SetupGuideLocationSGC4UiModel> { SetupGuideLocationSGC4UiModel.CurrentBluetoothState(it) }

    disposable.add(bluetoothStateObservable.bind(uiView.render()))
    return disposable
  }
}