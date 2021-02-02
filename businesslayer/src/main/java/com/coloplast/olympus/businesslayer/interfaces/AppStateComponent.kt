package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

interface AppStateComponent {

  fun insertAppState(appState: String): Completable
  fun deleteAppStatesAlreadySynced(): Observable<SyncResult>
  fun syncAppStatesWithCloud(userId: UUID, forceSync: Boolean = false) : Observable<SyncResult>
}