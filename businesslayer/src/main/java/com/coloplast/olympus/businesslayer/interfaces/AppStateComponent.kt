package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.businesslayer.models.SyncResult
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

interface AppStateComponent {

  fun insertAppState(appState: String): Completable
  fun deleteAppStatesAlreadySynced(): Observable<SyncResult>
  fun syncAppStatesWithCloud(userId: UUID) : Observable<SyncResult>
}