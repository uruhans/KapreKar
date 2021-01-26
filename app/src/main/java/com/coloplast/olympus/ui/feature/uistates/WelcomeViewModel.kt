package com.coloplast.olympus.ui.feature.uistates

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.GenericView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


class WelcomeViewModel(): BaseViewModel<GenericUiModel, GenericView>() {

  override fun bind(uiView: GenericView): Disposable {
    val disposable = CompositeDisposable()

    val stateUpdate = StateDispatcher.activeTransmitterStateChangeObservable()
      .filter { it is DataResult.Success }
      .map { it as DataResult.Success }
      .map { it.data }
      .map<GenericUiModel> { GenericUiModel.Success(it) }
    disposable.add(stateUpdate.bind(uiView.render()))
    return disposable
  }
}