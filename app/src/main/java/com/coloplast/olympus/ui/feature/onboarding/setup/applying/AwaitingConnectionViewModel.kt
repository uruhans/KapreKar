package com.coloplast.olympus.ui.feature.onboarding.setup.applying

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.onboarding.setup.model.AwaitingConnectionUiModel
import com.coloplast.olympus.ui.feature.transmitters.models.TransmittersUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AwaitingConnectionViewModel(
  private val transmitterComponent: TransmitterComponent
) : BaseViewModel<AwaitingConnectionUiModel, AwaitingConnectionView>() {
  override fun bind(uiView: AwaitingConnectionView): Disposable {
    val disposable = CompositeDisposable()

    // transmitters stored in database
    val transmittersOrEmptyObservable = transmitterComponent.getTransmitters()
      .subscribeOn(Schedulers.io())
      .map {
        if (it is DataResult.Success<*>) {
          it.data as List<Transmitter>
        }
        else {
          emptyList()
        }
      }

    // transforming the stored transmitters into UiModel to be displayed on the screen
    val transformedTransmitters = transmittersOrEmptyObservable
      .map {
        if (it.isEmpty()) {
          TransmittersUiModel.Empty()
        }
        else {
          TransmittersUiModel.TransmitterList(it)
        }
      }

    // a delayed subscription (others should only subscribe once transmitters have been returned to the controller)
    val delayedSubscriptionUntilCorrectReturnType =
      transformedTransmitters.filter { it is TransmittersUiModel.TransmitterList }

    val latestStateObservable = StateDispatcher.stateChangeObservable()
      .delaySubscription(delayedSubscriptionUntilCorrectReturnType)
      .delay(2, TimeUnit.SECONDS)
      .map { it.bleDevice.isAttached() }
      .filter { it }

    val takeUntilObservable = Observable.interval(60, TimeUnit.SECONDS)
      .map { false }

    val attachStateObservable = latestStateObservable.mergeWith(takeUntilObservable)
      .map { AwaitingConnectionUiModel(it) }
      .take(1)

    disposable.add(attachStateObservable.bind(uiView.render()))
    return disposable
  }
}