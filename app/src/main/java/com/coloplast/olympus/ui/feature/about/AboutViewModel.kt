package com.coloplast.olympus.ui.feature.about

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.GenericView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AboutViewModel(private val transmitterComponent: TransmitterComponent) : BaseViewModel<GenericUiModel, GenericView>() {

  override fun bind(uiView: GenericView): Disposable {
    val disposable = CompositeDisposable()

    val transmittersDataObservable = transmitterComponent.getTransmitters()
      .ofType(DataResult.Success::class.java)
      .map { it.data as List<Transmitter> }
      .map<GenericUiModel> { GenericUiModel.Success(it) }

    disposable.add(transmittersDataObservable.bind(uiView.render()))
    return disposable
  }
}