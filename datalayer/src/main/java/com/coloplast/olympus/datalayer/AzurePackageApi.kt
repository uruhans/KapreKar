package com.coloplast.olympus.datalayer

import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.types.PackageType
import io.reactivex.Completable
import java.util.*

interface AzurePackageApi {

  fun sendPackages(userId: UUID, packageType: PackageType, packages: List<TransmitterPackage>) : Completable
}