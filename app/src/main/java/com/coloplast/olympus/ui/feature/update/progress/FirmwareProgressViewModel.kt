package com.coloplast.olympus.ui.feature.update.progress

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.businesslayer.FirmwareManager
import com.coloplast.olympus.bluetooth.businesslayer.firmware.InvalidSignatureException
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FirmwareProgressViewModel(private val firmwareManager: FirmwareManager) : BaseViewModel<GenericUiModel, FirmwareProgressView>() {

  private val progressRelay = PublishRelay.create<Int>()
  private val downloadProgressValue = 25

  override fun bind(uiView: FirmwareProgressView): Disposable {
    val disposable = CompositeDisposable()

    val progressObservable = progressRelay
      .publish()
      .refCount()
      .map<GenericUiModel> { GenericUiModel.Success(it) }

    val macAddressObservable = uiView.updateFirmwareRequested()
      .switchMap { updateModel ->

        firmwareManager.initiateFirmwareDownload(updateModel.macAddress, updateModel.currentAppVersion)
          .subscribeOn(Schedulers.io())
          .doOnNext { progressRelay.accept(downloadProgressValue) }
          .flatMap {
            firmwareManager.updateFirmware(updateModel.macAddress, it)
              .map<GenericUiModel> { currentProgress ->
                // we need to convert the currentprogress from 0-100% into 0-75% (because the download progress is 25%)
                val updatedProgress = currentProgress.div(100).times(75).plus(downloadProgressValue)
                GenericUiModel.Success(updatedProgress)
              }
          }
          .onErrorReturn { throwable ->
            if(throwable is InvalidSignatureException) {
              GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 500)
            }
            else {
              GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 1000)
            }
          }
      }

    disposable.add(Observable.merge(progressObservable, macAddressObservable).bind(uiView.render()))
    return disposable
  }
}