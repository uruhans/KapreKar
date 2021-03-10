package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.AppStateComponent
import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.datalayer.AzurePackageApiImpl
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.shared.models.AppState
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.types.SyncState
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import java.util.*

class AppStateComponentImpl() : AppStateComponent {

  private val dbService = DbServiceImpl()
  private val azureService = AzurePackageApiImpl()
  private var lastAppState: AppState? = null

  override fun insertAppState(
    appState: String,
    logFromService: Boolean
  ): Completable {

    if (appState.isBlank()) {
      return Completable.complete()
    }

    val fromForeground = !AppSettings.appInBackground && !logFromService
    val appStatePackage =
      if (fromForeground) AppState(appState = appState)
      else AppState(internalAppState = appState)

    val isDuplicate = ((appStatePackage.appState == lastAppState?.appState) && (appStatePackage.internalAppState == lastAppState?.internalAppState))
    if (isDuplicate) {
      return Completable.complete()
    }

    lastAppState = appStatePackage
    return dbService.insertAppState(appStatePackage)
  }

  override fun deleteAppStatesAlreadySynced(): Observable<SyncResult> {
    return dbService.deleteAppStates()
        .toSingle<SyncResult> { SyncResult.Completed() }
        .toObservable()
        .onErrorReturn { SyncResult.Error(SyncResult.Error.State.DELETE_FAILED) }
  }

  override fun syncAppStatesWithCloud(userId: UUID): Observable<SyncResult> {
    return getPendingAppStates(userId)
      .doOnNext { Timber.d("Number of appStates to sync: ${it.size}") }
      .flatMap { appStates ->
        if (appStates.isEmpty()) {
          return@flatMap Observable.just(SyncResult.Completed(0))
        }
        else {
          azureService.sendAppStates(userId, appStates)
            .toSingle { true }
            .toObservable()
            .flatMap {
              dbService.updateSyncStateForAppStates(appStates, SyncState.Uploaded)
                .toSingle<SyncResult> { SyncResult.Completed() }
                .toObservable()
                .flatMap {
                  if (it is SyncResult.Completed) {
                    dbService.pendingAppStatesRemaining()
                      .toObservable()
                      .map<SyncResult> { count -> SyncResult.Completed(count) }
                  }
                  else {
                    Observable.just(it)
                  }
                }
            }
            .doOnError { Timber.e(it) }
            .onErrorResumeNext(
              dbService.updateSyncStateForAppStates(appStates, SyncState.Pending)
                .toSingle<SyncResult> { SyncResult.Error() }
                .toObservable()
            )
        }
      }
  }


  private fun getPendingAppStates(userId: UUID): Observable<List<AppState>> {
    return dbService.getPendingAppStates(userId)
      .map {
        if (it is DataResult.Success<*>) {
          it.data as List<AppState>
        }
        else {
          emptyList()
        }
      }
  }

}