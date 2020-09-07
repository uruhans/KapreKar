package com.coloplast.olympus.work

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class SyncViewModel(
  private val preferences: Preferences,
  private val userComponent: UserComponent,
  private val transmitterComponent: TransmitterComponent
) : BaseViewModel<GenericUiModel, SyncView>() {

  override fun bind(uiView: SyncView): Disposable {
    val disposable = CompositeDisposable()

    val initiateSyncObservable = uiView.beginSync()
      .switchMap {
        userComponent.getLocalUser(UUID.fromString(preferences.userLoggedIn))
          .filter { it.consent ?: false }
          .map { it.id }
          .flatMap { userId ->
            transmitterComponent.syncPackagesWithCloud(userId, PackageType.Raw)
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
      }

    disposable.add(initiateSyncObservable.bind(uiView.render()))
    return disposable
  }
}