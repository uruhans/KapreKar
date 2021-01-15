package com.coloplast.olympus.businesslayer.interfaces

import io.reactivex.Completable

interface AppStateComponent {

  fun insertAppStatePackage(appState: String, appInBackground: Boolean): Completable

//  fun deletePackagesAlreadySynced(packageType: PackageType): Observable<SyncResult>
//  fun syncPackagesWithCloud(userId: UUID, packageType: PackageType, forceSync: Boolean = false) : Observable<SyncResult>
//  fun pendingPackagesRemaining(userId: UUID, packageType: PackageType) : Single<Int>
//
}