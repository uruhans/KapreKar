package com.coloplast.olympus.datalayer

import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Completable

interface AzurePackageApi {

  fun sendPackages(userId: String, packageType: PackageType, packages: List<TransmitterPackage>) : Completable
}