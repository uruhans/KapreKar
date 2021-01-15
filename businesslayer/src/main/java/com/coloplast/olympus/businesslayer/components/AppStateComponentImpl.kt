package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.AppStateComponent
import com.coloplast.olympus.databaselayer.DbServiceImpl
import com.coloplast.olympus.datalayer.AzurePackageApiImpl
import com.coloplast.olympus.shared.models.AppStatePackage
import io.reactivex.Completable

class AppStateComponentImpl() : AppStateComponent {

  private val dbService = DbServiceImpl()
  private val azureService = AzurePackageApiImpl()

  override fun insertAppStatePackage(
    appState: String,
    internalAppState: String
  ): Completable {
    val appStatePackage = AppStatePackage(appState = appState, internalAppState = internalAppState)

    return dbService.insertAppStatePackage(appStatePackage)
  }
}