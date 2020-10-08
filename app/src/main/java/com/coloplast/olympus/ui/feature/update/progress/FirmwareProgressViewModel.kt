package com.coloplast.olympus.ui.feature.update.progress

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.FirmwareManager
import com.coloplast.olympus.bluetooth.businesslayer.firmware.InvalidSignatureException
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import com.coloplast.olympus.ui.feature.update.progress.model.FirmwareProgressUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class FirmwareProgressViewModel(private val firmwareManager: FirmwareManager) : BaseViewModel<FirmwareProgressUiModel, FirmwareProgressView>() {

  // relay to send back the progress after downloading firmware
  private val progressRelay = PublishRelay.create<Int>()

  // this is the progress as indicated *after* downloading the firmware from the server
  private val downloadProgressValue = 25

  override fun bind(uiView: FirmwareProgressView): Disposable {
    val disposable = CompositeDisposable()

    // a state observable to get the state for our specific tx
    val txStateObservable = StateDispatcher.stateChangeObservable()
      .filter { it.getMacAddress() == uiView.getUpdateModel().macAddress }

    // the relay to send back a progressUpdate after downloading the firmware
    val progressObservable = progressRelay
      .publish()
      .refCount()
      .map<FirmwareProgressUiModel> { FirmwareProgressUiModel.ProgressUpdate(it) }

    val macAddressObservable = txStateObservable
      // make sure we are in FIRMWARE_AVAILABLE state
      .filter { it.state == UiState.FIRMWARE_AVAILABLE }
      // and re-check if we are still in charging mode
      .filter { it.bleDevice.isCharging() }
      .flatMap {
        val updateModel = uiView.getUpdateModel()
        // initate firmware download
        firmwareManager.initiateFirmwareDownload(updateModel.macAddress, updateModel.currentAppVersion)
          .subscribeOn(Schedulers.io())
          // when download is complete send back a progress update to the controller
          .doOnNext { progressRelay.accept(downloadProgressValue) }
          .flatMap {
            // begin firmware update
            firmwareManager.updateFirmware(updateModel.macAddress, it)
              .map<FirmwareProgressUiModel> { currentProgress ->
                // we need to convert the currentprogress from 0-100% into 0-75% (because the download progress is 25%)
                val updatedProgress = currentProgress.toFloat().div(100f).times(75f).plus(downloadProgressValue)
                FirmwareProgressUiModel.ProgressUpdate(updatedProgress.toInt())
              }
          }
          .onErrorReturn { throwable ->
            if(throwable is InvalidSignatureException) {
              FirmwareProgressUiModel.InvalidFirmwareSignature()
            }
            else {
              FirmwareProgressUiModel.Error()
            }
          }
      }
      .startWith(FirmwareProgressUiModel.ProgressUpdate(0))

    // an observable to check that our transmitter has been correctly updated
    val updateFirmwareCompletedObservable = txStateObservable
      // we verify by checking that we are no longer in FIRMWARE_AVAILABLE state
      .filter { it.state != UiState.FIRMWARE_AVAILABLE }
      // send back an UpdateCompleted
      .map<FirmwareProgressUiModel> { FirmwareProgressUiModel.UpdateCompleted() }

    disposable.add(Observable.merge(progressObservable, macAddressObservable, updateFirmwareCompletedObservable).bind(uiView.render()))
    return disposable
  }
}