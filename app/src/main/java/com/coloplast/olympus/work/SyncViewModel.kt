package com.coloplast.olympus.work

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SyncViewModel(private val transmitterComponent: TransmitterComponent) : BaseViewModel<GenericUiModel, SyncView>() {

  override fun bind(uiView: SyncView): Disposable {
    val disposable = CompositeDisposable()

    val initiateSyncObservable = uiView.beginSync()
      .switchMap {
        transmitterComponent.syncPackagesWithCloud(PackageType.Raw)
          .subscribeOn(Schedulers.io())
          .map {
            if (it is SyncResult.Completed) {
              Timber.d("Tuples left [${it.tuplesRemaining}]")
              GenericUiModel.Success(it)
            }
            else {
              GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 3123)
            }
          }
      }

    disposable.add(initiateSyncObservable.bind(uiView.render()))
    return disposable
  }
}